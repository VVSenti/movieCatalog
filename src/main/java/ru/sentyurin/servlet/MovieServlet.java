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

import ru.sentyurin.service.MovieService;
import ru.sentyurin.service.impl.MovieServiceImpl;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.InconsistentInputException;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

/**
 * Servlet implementation class BooksController
 */
@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String JSON_MIME = "application/json";
	private static final String NOT_FOUND_BY_ID_MSG = "There is no movie with this ID";
	private static final String MUST_BE_ID_IN_PATH_VAR_MSG = "There must be path variable \"id\"";
	private static final String ID_FORMAT_ERROR_MSG = "Incorrect \"id\" path variable format";

	private final ObjectMapper objectMapper;
	private MovieService movieService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MovieServlet() {
		super();
		objectMapper = new ObjectMapper();
		movieService = new MovieServiceImpl();
	}

	/**
	 * A method to get JSON representation of all movie entities in repository or
	 * only one with specified ID. A value of ID should be given as path variable,
	 * e.g /movies?id=1.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see MovieOutgoingDto
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("id") != null) {
			doGetById(request, response);
			return;
		}
		response.setContentType(JSON_MIME);
		response.getWriter().print(objectMapper.writeValueAsString(movieService.getMovies()));
	}

	/**
	 * A method to create a new movie entity in repository.
	 * 
	 * Input JSON is mapped to {@code MovieIncomingDto}. An example of
	 * JSON in put in HTTP body: {"title":"Reservoir dogs", "releaseYear":1992,
	 * "directorName":"Quentin Tarantino"}
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see MovieIncomingDto
	 * @see MovieOutgoingDto
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String json = request.getReader().lines().collect(Collectors.joining("\n"));
		try {
			MovieIncomingDto movieIncomingDto = objectMapper.readValue(json,
					MovieIncomingDto.class);
			MovieOutgoingDto movie = movieService.createMovie(movieIncomingDto);
			response.setContentType(JSON_MIME);
			response.getWriter().print(objectMapper.writeValueAsString(movie));
			response.setStatus(201);
		} catch (JsonProcessingException e) {
			response.setStatus(400);
			response.getWriter().print("Bad input JSON. " + e.getMessage());
		} catch (IncompleateInputExeption e) {
			response.setStatus(400);
			response.getWriter().print("Incompleate data: " + e.getMessage());
		} catch (NoDataInRepositoryException e) {
			response.setStatus(400);
			response.getWriter().print("Incorrect data: " + e.getMessage());
		} catch (InconsistentInputException e) {
			response.setStatus(400);
			response.getWriter().print("Inconsistent data: " + e.getMessage());
		}
	}

	/**
	 * A method to update an existing movie entity in repository.
	 * 
	 * Input JSON is be mapped to {@code MovieIncomingDto}. An example of
	 * JSON in put in HTTP body: {"id":1, "title":"Reservoir dogs",
	 * "releaseYear":1992, "directorId":1, "directorName":"Quentin Tarantino"}.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * @see MovieIncomingDto
	 * @see MovieOutgoingDto
	 **/
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String json = request.getReader().lines().collect(Collectors.joining("\n"));
			MovieIncomingDto movieIncomingDto = objectMapper.readValue(json,
					MovieIncomingDto.class);
			MovieOutgoingDto movie = movieService.updateMovie(movieIncomingDto);
			response.setContentType(JSON_MIME);
			response.getWriter().print(objectMapper.writeValueAsString(movie));
		} catch (JsonProcessingException e) {
			response.setStatus(400);
			response.getWriter().print("Bad input JSON. " + e.getMessage());
		} catch (IncompleateInputExeption e) {
			response.setStatus(400);
			response.getWriter().print("Incompleate data: " + e.getMessage());
		} catch (IncorrectInputException e) {
			response.setStatus(400);
			response.getWriter().print("Incorrect data: " + e.getMessage());
		} catch (InconsistentInputException e) {
			response.setStatus(400);
			response.getWriter().print("Inconsistent data: " + e.getMessage());
		} catch (NoDataInRepositoryException e) {
			response.setStatus(404);
			response.getWriter().print(e.getMessage());
		}
	}

	/**
	 * A method to delete an existing movie entity with specified ID in repository.
	 * A value of ID should be given as path variable, e.g /movies?id=1.
	 * 
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer movieId = getIdFromPathVariableOrSetErrorInResponse(request, response);
		if (movieId == null)
			return;

		boolean resultStatus = movieService.deleteMovie(movieId);
		if (resultStatus) {
			response.setStatus(200);
			response.getWriter().printf("Movie with id %d has been deleted", movieId);
		} else {
			response.setStatus(404);
			response.getWriter().print(NOT_FOUND_BY_ID_MSG);
		}

	}

	/**
	 * Sets {@code MovieRepository}
	 * 
	 * @param movieService
	 */
	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}

	private void doGetById(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Integer movieId = getIdFromPathVariableOrSetErrorInResponse(request, response);
		if (movieId == null)
			return;

		Optional<MovieOutgoingDto> optionalMovie = movieService.getMovieById(movieId);
		if (optionalMovie.isEmpty()) {
			response.setStatus(404);
			response.getWriter().print(NOT_FOUND_BY_ID_MSG);
			return;
		}

		response.setContentType(JSON_MIME);
		response.getWriter().print(objectMapper.writeValueAsString(optionalMovie.get()));
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
			response.getWriter().print(MUST_BE_ID_IN_PATH_VAR_MSG);
		} catch (NumberFormatException e) {
			response.setStatus(400);
			response.getWriter().print(ID_FORMAT_ERROR_MSG);
		}
		return movieId;
	}
}