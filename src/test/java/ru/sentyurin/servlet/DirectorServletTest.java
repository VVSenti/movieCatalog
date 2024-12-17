package ru.sentyurin.servlet;

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

import ru.sentyurin.service.DirectorService;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

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
		service = Mockito.mock(DirectorService.class);
		
		Field serviceField;
		try {
			serviceField = DirectorServlet.class.getDeclaredField("directorService");
			serviceField.setAccessible(true);
			serviceField.set(servlet, service);
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
	void shouldReturnAllDirectors() throws ServletException, IOException {
		List<DirectorOutgoingDto> directorsOutgoingDtos = List.of(new DirectorOutgoingDto(),
				new DirectorOutgoingDto());
		Mockito.when(service.getDirectors()).thenReturn(directorsOutgoingDtos);

		Mockito.when(request.getParameter("id")).thenReturn(null);
		servlet.doGet(request, response);
		List<DirectorOutgoingDto> directorsDtos = objectMapper.readValue(
				responseStringWriter.toString(), new TypeReference<List<DirectorOutgoingDto>>() {
				});
		assertEquals(directorsOutgoingDtos.size(), directorsDtos.size());
	}

	@Test
	void shouldReturnDirectorById() throws ServletException, IOException {
		Integer directorIdToGet = 2;
		Mockito.when(request.getParameter("id")).thenReturn(directorIdToGet.toString());

		Mockito.when(service.getDirectorById(directorIdToGet))
				.thenReturn(Optional.of(new DirectorOutgoingDto()));

		servlet.doGet(request, response);
		DirectorOutgoingDto directorDto = objectMapper.readValue(responseStringWriter.toString(),
				DirectorOutgoingDto.class);
		assertNotNull(directorDto);
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
		Integer directorIdToGet = 4;
		Mockito.when(request.getParameter("id")).thenReturn(directorIdToGet.toString());
		Mockito.when(service.getDirectorById(directorIdToGet)).thenReturn(Optional.empty());
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
		Integer directorIdToDelete = 2;
		Mockito.when(request.getParameter("id")).thenReturn(directorIdToDelete.toString());
		Mockito.when(service.deleteDirector(directorIdToDelete)).thenReturn(false);
		servlet.doDelete(request, response);
		assertEquals(404, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenDelete() throws ServletException, IOException {
		Integer directorIdToDelete = 2;
		Mockito.when(request.getParameter("id")).thenReturn(directorIdToDelete.toString());
		Mockito.when(service.deleteDirector(directorIdToDelete)).thenReturn(true);
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
		String json = objectMapper.writeValueAsString(new DirectorIncomingDto());
		Mockito.when(service.createDirector(Mockito.any(DirectorIncomingDto.class)))
				.thenReturn(new DirectorOutgoingDto());

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		servlet.doPost(request, response);
		assertEquals(201, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithInvalidJsonRequestBody()
			throws IOException, ServletException {
		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader("asd")));
		servlet.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenPostWithIncompleateDataInput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new DirectorIncomingDto());
		Mockito.when(service.createDirector(Mockito.any(DirectorIncomingDto.class)))
				.thenThrow(new IncompleateInputExeption(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		servlet.doPost(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldCorrectlyUpdateDirectorById() throws ServletException, IOException {
		String json = objectMapper.writeValueAsString(new DirectorIncomingDto());
		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

		Integer directorIdToUpdate = 2;
		Mockito.when(request.getParameter("id")).thenReturn(directorIdToUpdate.toString());
		Mockito.when(service.updateDirector(Mockito.any(DirectorIncomingDto.class)))
				.thenReturn(new DirectorOutgoingDto());
		servlet.doPut(request, response);
		DirectorOutgoingDto directorDto = objectMapper.readValue(responseStringWriter.toString(),
				DirectorOutgoingDto.class);
		assertNotNull(directorDto);
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithIncompleateDataInput()
			throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new DirectorIncomingDto());
		Mockito.when(service.updateDirector(Mockito.any(DirectorIncomingDto.class)))
				.thenThrow(new IncompleateInputExeption(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithInvalidJsonRequestBody()
			throws IOException, ServletException {
		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader("asd")));
		servlet.doPut(request, response);
		assertEquals(400, responseStatus.get());
	}

	@Test
	void shouldReturnCorrectStatusWhenUpdateWithInvalidId() throws IOException, ServletException {
		String json = objectMapper.writeValueAsString(new DirectorIncomingDto());
		Mockito.when(service.updateDirector(Mockito.any(DirectorIncomingDto.class)))
				.thenThrow(new NoDataInRepositoryException(""));

		Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		servlet.doPut(request, response);
		assertEquals(404, responseStatus.get());
	}

}