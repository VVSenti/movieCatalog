package ru.sentyurin.servlet;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.service.DirectorService;
import ru.sentyurin.service.impl.DirectorServiceImpl;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

/**
 * Servlet implementation class BooksController
 */
@WebServlet("/directors")
public class DirectorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String JSON_MIME = "application/json";
	private static final String NO_DIRECTOR_WITH_ID_MSG = "There is no director with this ID";
	private static final String MUST_BE_ID_IN_PATH_VAR = "There must be path variable \"id\"";
	private static final String ID_FORMAT_ERROR = "Incorrect \"id\" path variable format";

	private final ObjectMapper objectMapper;
	private DirectorService directorService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DirectorServlet() {
		super();
		objectMapper = new ObjectMapper();
		directorService = new DirectorServiceImpl();
	}

	/**
	 * Method to get JSON representation of all director entities in repository or
	 * only one with specified ID. A value of ID should be given as path variable,
	 * e.g /directors?id=1.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see DirectorOutgoingDto
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("id") != null) {
			doGetById(request, response);
			return;
		}
		response.setContentType(JSON_MIME);
		response.getWriter().print(objectMapper.writeValueAsString(directorService.getDirectors()));
	}

	/**
	 * Method to create a new director entity in repository.
	 * 
	 * Input JSON is mapped to {@code DirectorIncomingDto}. An example of JSON in
	 * put in HTTP body: {"name":"Quentin Tarantino"}
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see DirectorIncomingDto
	 * @see DirectorOutgoingDto
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String json = request.getReader().lines().collect(Collectors.joining("\n"));
			DirectorIncomingDto incomingDto = objectMapper.readValue(json,
					DirectorIncomingDto.class);
			DirectorOutgoingDto director = directorService.createDirector(incomingDto);
			response.setContentType(JSON_MIME);
			response.getWriter().print(objectMapper.writeValueAsString(director));
			response.setStatus(201);
		} catch (JsonProcessingException e) {
			response.setStatus(400);
			response.getWriter().print("Bad input JSON. " + e.getMessage());
		} catch (IncompleateInputExeption e) {
			response.setStatus(400);
			response.getWriter().print("Incompleate data: " + e.getMessage());
		}
	}

	/**
	 * A method to update an existing director entity in repository.
	 * 
	 * Input JSON is mapped to {@code DirectorIncomingDto}. An example of JSON in
	 * put in HTTP body: {"id":1, "name":"Quentin Tarantino"}.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see DirectorIncomingDto
	 * @see DirectorOutgoingDto
	 **/
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String json = request.getReader().lines().collect(Collectors.joining("\n"));
			DirectorIncomingDto incomingDto = objectMapper.readValue(json,
					DirectorIncomingDto.class);
			DirectorOutgoingDto director = directorService.updateDirector(incomingDto);
			response.setContentType(JSON_MIME);
			response.getWriter().print(objectMapper.writeValueAsString(director));
		} catch (JsonProcessingException e) {
			response.setStatus(400);
			response.getWriter().print("Bad input JSON. " + e.getMessage());
		} catch (IncompleateInputExeption e) {
			response.setStatus(400);
			response.getWriter().print("Incompleate data: " + e.getMessage());
		} catch (NoDataInRepositoryException e) {
			response.setStatus(404);
			response.getWriter().print(NO_DIRECTOR_WITH_ID_MSG);
		}
	}

	/**
	 * A method to delete an existing director entity with specified ID in
	 * repository. A value of ID should be given as path variable, e.g
	 * /directors?id=1.
	 * 
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer directorId = getIdFromPathVariableOrSetErrorInResponse(request, response);
		if (directorId == null)
			return;

		boolean resultStatus = directorService.deleteDirector(directorId);
		if (resultStatus) {
			response.setStatus(200);
			response.getWriter().printf("Director with id %d has been deleted", directorId);
		} else {
			response.setStatus(404);
			response.getWriter().print(NO_DIRECTOR_WITH_ID_MSG);
		}
	}

	/**
	 * Sets {@code MovieRepository}
	 * 
	 * @param movieService
	 */
	public void setDirectorService(DirectorService directorService) {
		this.directorService = directorService;
	}

	private void doGetById(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Integer directorId = getIdFromPathVariableOrSetErrorInResponse(request, response);
		if (directorId == null)
			return;

		Optional<DirectorOutgoingDto> optionalDirector = directorService
				.getDirectorById(directorId);
		if (optionalDirector.isEmpty()) {
			response.setStatus(404);
			response.getWriter().print(NO_DIRECTOR_WITH_ID_MSG);
			return;
		}
		response.setContentType(JSON_MIME);
		objectMapper.writeValue(response.getWriter(), optionalDirector.get());
	}

	private Integer getIdFromPathVariableOrSetErrorInResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Integer movieId = null;
		try {
			String movieIdAsString = request.getParameter("id");
			Objects.requireNonNull(movieIdAsString);
			movieId = Integer.parseInt(request.getParameter("id"));
		} catch (NullPointerException e) {
			response.setStatus(400);
			response.getWriter().print(MUST_BE_ID_IN_PATH_VAR);
		} catch (NumberFormatException e) {
			response.setStatus(400);
			response.getWriter().print(ID_FORMAT_ERROR);
		}
		return movieId;
	}

}
