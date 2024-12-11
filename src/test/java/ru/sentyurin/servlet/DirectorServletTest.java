package ru.sentyurin.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.model.Director;
import ru.sentyurin.service.DirectorService;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;
import ru.sentyurin.servlet.mapper.DirectorDtoMapper;
import ru.sentyurin.servlet.mapper.DirectorDtoMapperImpl;
import ru.sentyurin.util.exсeption.IncompleateInputExeption;
import ru.sentyurin.util.exсeption.NoDataInRepository;

class DirectorServletTest {
	private DirectorService service;
	private DirectorServlet servlet;
	private StringWriter responseStringWriter;
	private PrintWriter responsePrintWriter;
	private ObjectMapper objectMapper = new ObjectMapper();
	private HttpServletResponse response;
	private AtomicInteger responseStatus;
	private HttpServletRequest request;

	@BeforeEach
	void init() throws IOException {
		servlet = new DirectorServlet();
		service = new DirectorServiceMock();
		servlet.setDirectorService(service);

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
	void shouldReturnAllDirectors() throws ServletException, IOException {
		Mockito.when(request.getParameter("id")).thenReturn(null);

		servlet.doGet(request, response);
		List<DirectorOutgoingDto> movieDtos = objectMapper.readValue(
				responseStringWriter.toString(), new TypeReference<List<DirectorOutgoingDto>>() {
				});

		assertEquals(service.getDirectors().size(), movieDtos.size());
	}

	@Test
	void shouldReturnDirectorById() throws ServletException, IOException {
		int movieIdToGet = 2;
		Mockito.when(request.getParameter("id")).thenReturn(movieIdToGet + "");
		servlet.doGet(request, response);
		DirectorOutgoingDto movieDto = objectMapper.readValue(responseStringWriter.toString(),
				DirectorOutgoingDto.class);
		DirectorOutgoingDto expectedMovie = service.getDirectorById(movieIdToGet).get();
		assertEquals(expectedMovie.getId(), movieDto.getId());
		assertEquals(expectedMovie.getName(), movieDto.getName());
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
		String correctJson = "{\"id\" : 3, \"name\" : \"Edward Berger\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(correctJson)));
		servlet.doPost(request, response);
		assertEquals(201, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithInvalidJsonRequestBody()
			throws IOException, ServletException {
		String jsonCorruptedDirectorNameFieldName = "{\"id\" : 3, \"nama\" : \"Edward Berger\"}";
		Mockito.when(request.getReader()).thenReturn(
				new BufferedReader(new StringReader(jsonCorruptedDirectorNameFieldName)));
		servlet.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldCorrectlyUpdateDirectorById() throws ServletException, IOException {
		int directorIdToUpdate = 2;
		DirectorOutgoingDto updatedDirector = service.getDirectorById(directorIdToUpdate).get();
		updatedDirector.setName("Vasia Ivanov");

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(
				"{\"id\" : " + directorIdToUpdate + ", \"name\" : \"Vasia Ivanov\"}")));

		Mockito.when(request.getParameter("id")).thenReturn(directorIdToUpdate + "");
		servlet.doPut(request, response);

		DirectorOutgoingDto movieDto = objectMapper.readValue(responseStringWriter.toString(),
				DirectorOutgoingDto.class);
		assertEquals(updatedDirector.getId(), movieDto.getId());
		assertEquals(updatedDirector.getName(), movieDto.getName());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutIfNoIdInJsonRequestBody()
			throws IOException, ServletException {
		String jsonWithoutIdField = "{\"name\" : \"Edward Berger\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithoutIdField)));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutIfBadJsonRequestBody()
			throws IOException, ServletException {
		String jsonWithoutBrackets = "\"name\" : \"Edward Berger\"";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithoutBrackets)));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPutWithInvalidId() throws IOException, ServletException {
		String jsonWithoutBrackets = "{\"id\" : 5, \"name\" : \"Krzysztof Kieslowski\"}";
		Mockito.when(request.getReader())
				.thenReturn(new BufferedReader(new StringReader(jsonWithoutBrackets)));
		servlet.doPut(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostIfNoNameInJsonRequestBody()
			throws IOException, ServletException {
		String jsonWithoutTitleField = "{\"id\" : 3}";

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

class DirectorServiceMock implements DirectorService {
	private Map<Integer, Director> directors;
	private final DirectorDtoMapper dtoMapper;

	public DirectorServiceMock() {
		directors = new HashMap<>();
		directors.put(1, new Director(1, "Quentin Tarantino", null));
		directors.put(2, new Director(2, "Christopher Nolan", null));
		dtoMapper = new DirectorDtoMapperImpl();
	}

	@Override
	public DirectorOutgoingDto createDirector(DirectorIncomingDto director) {
		directorDataValidation(director);
		Director newDirector = dtoMapper.map(director);
		directors.put(newDirector.getId(), newDirector);
		return mapToOutgoingDto(newDirector);
	}

	@Override
	public List<DirectorOutgoingDto> getDirectors() {
		return directors.values().stream().map(this::mapToOutgoingDto).toList();
	}

	@Override
	public Optional<DirectorOutgoingDto> getDirectorById(int id) {
		Director director = directors.get(id);
		return director == null ? Optional.empty() : Optional.of(mapToOutgoingDto(director));
	}

	@Override
	public DirectorOutgoingDto updateDirector(DirectorIncomingDto director) {
		if (director.getId() == null)
			throw new IncompleateInputExeption("There must be a director ID");
		directorDataValidation(director);
		Director newDirector = mapFromIncomingDto(director);
		if (!directors.containsKey(newDirector.getId())) {
			throw new NoDataInRepository("There is no director with this ID");
		}
		directors.put(director.getId(), newDirector);
		return mapToOutgoingDto(newDirector);
	}

	@Override
	public boolean deleteDirector(int id) {
		return directors.remove(id) != null;
	}

	private void directorDataValidation(DirectorIncomingDto director) {
		if (director.getName() == null)
			throw new IncompleateInputExeption("There must be a director name");
	}

	private DirectorOutgoingDto mapToOutgoingDto(Director director) {
		return dtoMapper.map(director);
	}

	private Director mapFromIncomingDto(DirectorIncomingDto incomingDto) {
		return dtoMapper.map(incomingDto);
	}

}
