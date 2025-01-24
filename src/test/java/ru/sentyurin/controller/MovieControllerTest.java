package ru.sentyurin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.controller.MovieController;
import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.InconsistentInputException;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

class MovieControllerTest {

	private MovieService service;
	private MovieController controller;
	private StringWriter responseStringWriter;
	private PrintWriter responsePrintWriter;
	private ObjectMapper objectMapper;
	private HttpServletResponse response;
	private AtomicInteger responseStatus;
	private HttpServletRequest request;

	@BeforeEach
	void init() throws IOException {

		objectMapper = new ObjectMapper();

//		servlet = new MovieServlet();
		controller = null;
		service = Mockito.mock(MovieService.class);

		Field serviceField;
		try {
			serviceField = MovieController.class.getDeclaredField("movieService");
			serviceField.setAccessible(true);
			serviceField.set(controller, service);
			serviceField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		List<MovieOutgoingDto> moviesOutgoingDtos = List.of(new MovieOutgoingDto(),
				new MovieOutgoingDto());
		Mockito.when(service.getMovies()).thenReturn(moviesOutgoingDtos);

		Mockito.when(request.getParameter("id")).thenReturn(null);
		controller.doGet(request, response);
		List<MovieOutgoingDto> movieDtos = objectMapper.readValue(responseStringWriter.toString(),
				new TypeReference<List<MovieOutgoingDto>>() {
				});

		assertEquals(moviesOutgoingDtos.size(), movieDtos.size());
	}

	@Test
	void shouldReturnMovieById() throws ServletException, IOException {
		Integer movieIdToGet = 2;
		Mockito.when(request.getParameter("id")).thenReturn(movieIdToGet.toString());
		Mockito.when(service.getMovieById(movieIdToGet))
				.thenReturn(Optional.of(new MovieOutgoingDto()));

		controller.doGet(request, response);
		MovieOutgoingDto movieDto = objectMapper.readValue(responseStringWriter.toString(),
				MovieOutgoingDto.class);
		assertNotNull(movieDto);
	}

	@Test
	void shouldReturnCorrectStatusWhenGetWithIncorrrectIdFormat()
			throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn("4.5");
		controller.doGet(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenGetWithInvalidId() throws ServletException, IOException {
		Integer movieIdToGet = 2;
		Mockito.when(request.getParameter("id")).thenReturn(movieIdToGet.toString());
		Mockito.when(service.getMovieById(movieIdToGet)).thenReturn(Optional.empty());
		controller.doGet(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDeleteWithIncorrrectIdFormat()
			throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn("4.5");
		controller.doDelete(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDeleteWithInvalidValue()
			throws ServletException, IOException {
		Integer movieIdToDelete = 2;
		Mockito.when(request.getParameter("id")).thenReturn(movieIdToDelete.toString());
		Mockito.when(service.deleteMovie(movieIdToDelete)).thenReturn(false);
		controller.doDelete(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDelete() throws ServletException, IOException {
		Integer movieIdToDelete = 2;
		Mockito.when(request.getParameter("id")).thenReturn(movieIdToDelete.toString());
		Mockito.when(service.deleteMovie(movieIdToDelete)).thenReturn(true);
		controller.doDelete(request, response);
		assertEquals(200, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDeleteWithoutIdPathVariable()
			throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn(null);
		controller.doDelete(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldWorkCorrectlyWhenPost() throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.createMovie(Mockito.any(MovieIncomingDto.class)))
				.thenReturn(new MovieOutgoingDto());

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPost(request, response);
		assertEquals(201, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithInvalidJsonRequestBody()
			throws IOException, ServletException {
		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader("asd")));
		controller.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithIncompleateDataInput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.createMovie(Mockito.any(MovieIncomingDto.class)))
				.thenThrow(new IncompleateInputExeption(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithIncorrectInput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.createMovie(Mockito.any(MovieIncomingDto.class)))
				.thenThrow(new NoDataInRepositoryException(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithInconsistentDataInput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.createMovie(Mockito.any(MovieIncomingDto.class)))
				.thenThrow(new InconsistentInputException(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldCorrectlyUpdateMovieById() throws ServletException, IOException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

		Integer movieIdToUpdate = 2;
		Mockito.when(request.getParameter("id")).thenReturn(movieIdToUpdate.toString());
		Mockito.when(service.updateMovie(Mockito.any(MovieIncomingDto.class)))
				.thenReturn(new MovieOutgoingDto());
		controller.doPut(request, response);
		MovieOutgoingDto movieDto = objectMapper.readValue(responseStringWriter.toString(),
				MovieOutgoingDto.class);
		assertNotNull(movieDto);
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithIncorrectDataInput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.updateMovie(Mockito.any(MovieIncomingDto.class)))
				.thenThrow(new IncorrectInputException(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithInvalidJsonRequestBody()
			throws IOException, ServletException {
		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader("asd")));
		controller.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithIncompleateDataInput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.updateMovie(Mockito.any(MovieIncomingDto.class)))
				.thenThrow(new IncompleateInputExeption(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithInvalidId() throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.updateMovie(Mockito.any(MovieIncomingDto.class)))
				.thenThrow(new NoDataInRepositoryException(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPut(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithInconsistentDatainput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new MovieIncomingDto());
		Mockito.when(service.updateMovie(Mockito.any(MovieIncomingDto.class)))
				.thenThrow(new InconsistentInputException(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		controller.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

}
