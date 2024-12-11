package ru.sentyurin.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.servlet.mapper.MovieDtoMapper;
import ru.sentyurin.servlet.mapper.MovieDtoMapperImpl;
import ru.sentyurin.util.exсeption.IncompleateInputExeption;
import ru.sentyurin.util.exсeption.IncorrectInputException;
import ru.sentyurin.util.exсeption.NoDataInRepository;

class MovieServletTest {

	private MovieDtoMapper dtoMapper;

	private MovieService service;
	private MovieServlet servlet;
	private StringWriter responseStringWriter;
	private PrintWriter responsePrintWriter;
	private ObjectMapper objectMapper = new ObjectMapper();
	private HttpServletResponse response;
	private AtomicInteger responseStatus;
	private HttpServletRequest request;

	@BeforeEach
	void init() throws IOException {
		dtoMapper = new MovieDtoMapperImpl();

		servlet = new MovieServlet();
		service = new MovieServiceMock();
		servlet.setMovieService(service);

		response = Mockito.mock(HttpServletResponse.class);
		responseStringWriter = new StringWriter();
		responsePrintWriter = new PrintWriter(responseStringWriter);
		Mockito.when(response.getWriter()).thenReturn(responsePrintWriter);

		responseStatus = new AtomicInteger();
		Mockito.doAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) {
				return responseStatus.getAndSet((Integer) invocation.getArguments()[0]);
			}
		}).when(response).setStatus(Mockito.anyInt());

		request = Mockito.mock(HttpServletRequest.class);
	}

	@Test
	void shouldReturnAllMovies() throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn(null);

		servlet.doGet(request, response);
		List<MovieOutgoingDto> movieDtos = objectMapper.readValue(responseStringWriter.toString(),
				new TypeReference<List<MovieOutgoingDto>>() {
				});

		assertEquals(service.getMovies().size(), movieDtos.size());
	}

	@Test
	void shouldReturnMovieById() throws ServletException, IOException {
		int movieIdToGet = 2;
		Mockito.when(request.getParameter("id")).thenReturn(movieIdToGet + "");
		servlet.doGet(request, response);
		MovieOutgoingDto movieDto = objectMapper.readValue(responseStringWriter.toString(),
				MovieOutgoingDto.class);
		MovieOutgoingDto expectedMovie = service.getMovieById(movieIdToGet).get();
		assertEquals(expectedMovie.getId(), movieDto.getId());
		assertEquals(expectedMovie.getTitle(), movieDto.getTitle());
		assertEquals(expectedMovie.getReleaseYear(), movieDto.getReleaseYear());
		assertEquals(expectedMovie.getDirectorId(), movieDto.getDirectorId());
		assertEquals(expectedMovie.getDirectorName(), movieDto.getDirectorName());
	}

	@Test
	void shouldReturnCorrectStatusWhenGetWithIncorrrectIdFormat()
			throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn("4.5");
		servlet.doGet(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenGetWithInvalidId() throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn("4");
		servlet.doGet(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDeleteWithIncorrrectIdFormat()
			throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn("4.5");
		servlet.doDelete(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDeleteWithInvalidValue()
			throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn("4");
		servlet.doDelete(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDelete() throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn("1");
		servlet.doDelete(request, response);
		assertEquals(200, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDeleteWithoutIdPathVariable()
			throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn(null);
		servlet.doDelete(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldWorkCorrectlyWhenPost() throws IOException, ServletException {
		String correctJson = "{\"id\" : 4, \"title\" : \"Jackie Brown\", "
				+ "\"releaseYear\" : 1997, \"directorName\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(correctJson)));
		servlet.doPost(request, response);
		assertEquals(201, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithInvalidReleaseYear()
			throws IOException, ServletException {
		String jsonWithInvalidYear = "{\"id\" : 4, \"title\" : \"Jackie Brown\", "
				+ "\"releaseYear\" : 1000, \"directorName\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithInvalidYear)));
		servlet.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithInvalidJsonRequestBody()
			throws IOException, ServletException {
		String jsonCorruptedDirectorNameFieldName = "{\"id\" : 4, \"title\" : \"Jackie Brown\", "
				+ "\"releaseYear\" : 1000, \"director\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader()).thenReturn(
				new BufferedReader(new StringReader(jsonCorruptedDirectorNameFieldName)));
		servlet.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldCorrectlyUpdateMovieById() throws ServletException, IOException {
		int movieIdToUpdate = 2;
		MovieOutgoingDto updatedMovie = service.getMovieById(movieIdToUpdate).get();
		updatedMovie.setTitle("NewTitle");
		updatedMovie.setReleaseYear(2000);

		ObjectMapper objectMapperToJson = new ObjectMapper();
		Mockito.when(request.getReader()).thenReturn(new BufferedReader(
				new StringReader(objectMapperToJson.writeValueAsString(updatedMovie))));

		Mockito.when(request.getParameter("id")).thenReturn(movieIdToUpdate + "");
		servlet.doPut(request, response);
		MovieOutgoingDto movieDto = objectMapper.readValue(responseStringWriter.toString(),
				MovieOutgoingDto.class);
		assertEquals(updatedMovie.getId(), movieDto.getId());
		assertEquals(updatedMovie.getTitle(), movieDto.getTitle());
		assertEquals(updatedMovie.getReleaseYear(), movieDto.getReleaseYear());
		assertEquals(updatedMovie.getDirectorId(), movieDto.getDirectorId());
		assertEquals(updatedMovie.getDirectorName(), movieDto.getDirectorName());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutWithInvalidReleaseYear()
			throws IOException, ServletException {
		String jsonWithInvalidYear = "{\"id\" : 4, \"title\" : \"Jackie Brown\", "
				+ "\"releaseYear\" : 1000, \"directorName\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithInvalidYear)));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutWithInvalidJsonRequestBody()
			throws IOException, ServletException {
		String jsonCorruptedDirectorNameFieldName = "{\"id\" : 4, \"title\" : \"Jackie Brown\", "
				+ "\"releaseYear\" : 1000, \"director\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader()).thenReturn(
				new BufferedReader(new StringReader(jsonCorruptedDirectorNameFieldName)));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutIfNoIdInJsonRequestBody()
			throws IOException, ServletException {
		String jsonWithoutIdField = "{\"title\" : \"Jackie Brown\", "
				+ "\"releaseYear\" : 1992, \"directorName\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithoutIdField)));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutWithInvalidId() throws IOException, ServletException {
		String jsonWithoutIdField = "{\"id\" : 8, \"title\" : \"Jackie Brown\", "
				+ "\"releaseYear\" : 1992, \"directorName\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithoutIdField)));
		servlet.doPut(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutIfNoTitleInJsonRequestBody()
			throws IOException, ServletException {
		String jsonWithoutTitleField = "{\"id\" : 4, "
				+ "\"releaseYear\" : 1997, \"directorName\" : \"Quentin Tarantino\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithoutTitleField)));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostIfNoTitleInJsonRequestBody()
			throws IOException, ServletException {
		String jsonWithoutTitleField = "{\"id\" : 4, "
				+ "\"releaseYear\" : 1997, \"directorName\" : \"Quentin Tarantino\"}";

		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithoutTitleField)));
		servlet.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}
	
	@Test
	void initShouldWorkCorrectly() throws ServletException {
		servlet.init();
	}

}

class MovieServiceMock implements MovieService {

	private Map<Integer, Movie> movies;
	private final MovieDtoMapper dtoMapper;

	public MovieServiceMock() {
		movies = new HashMap<>();
		Director director = new Director(1, "Quentin Tarantino", null);
		movies.put(1, new Movie(1, "Reservoir Dogs", 1992, director));
		movies.put(2, new Movie(2, "Pulp Fiction", 1994, director));
		movies.put(3, new Movie(3, "Django Unchained", 2012, director));
		dtoMapper = new MovieDtoMapperImpl();
	}

	@Override
	public MovieOutgoingDto createMovie(MovieIncomingDto movie) {
		movieDataValidation(movie);
		Movie newMovie = dtoMapper.map(movie);
		movies.put(newMovie.getId(), newMovie);
		return dtoMapper.map(newMovie);
	}

	@Override
	public List<MovieOutgoingDto> getMovies() {
		return movies.values().stream().map(this::mapToOutgoingDto).toList();
	}

	@Override
	public Optional<MovieOutgoingDto> getMovieById(int id) {
		Movie movie = movies.get(id);
		return movie == null ? Optional.empty() : Optional.of(mapToOutgoingDto(movie));
	}

	@Override
	public MovieOutgoingDto updateMovie(MovieIncomingDto movie) {
		if (movie.getId() == null)
			throw new IncompleateInputExeption("There must be a movie ID");
		movieDataValidation(movie);
		Movie newMovie = mapFromIncomingDto(movie);
		if (!movies.containsKey(newMovie.getId())) {
			throw new NoDataInRepository("There is no movie with this ID");
		}
		movies.put(movie.getId(), newMovie);
		return mapToOutgoingDto(newMovie);
	}

	@Override
	public boolean deleteMovie(int id) {
		return movies.remove(id) != null;
	}

	private void movieDataValidation(MovieIncomingDto movie) {
		if (movie.getTitle() == null)
			throw new IncompleateInputExeption("There must be a movie title");
		if (movie.getReleaseYear() == null)
			throw new IncompleateInputExeption("There must be a release year");
		if (movie.getReleaseYear() < 1895)
			throw new IncorrectInputException(
					"A release year is less than 1895. It is unacceptably suspicious");
		if (movie.getDirectorId() == null && movie.getDirectorName() == null)
			throw new IncompleateInputExeption("There must be director ID or their name");
	}

	private MovieOutgoingDto mapToOutgoingDto(Movie movie) {
		return dtoMapper.map(movie);
	}

	private Movie mapFromIncomingDto(MovieIncomingDto incomingDto) {
		return dtoMapper.map(incomingDto);
	}

}
