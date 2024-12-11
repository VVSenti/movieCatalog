package ru.sentyurin.servlet;

import java.io.IOException;
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
import ru.sentyurin.util.exсeption.IncompleateInputExeption;
import ru.sentyurin.util.exсeption.InconsistentInputException;
import ru.sentyurin.util.exсeption.IncorrectInputException;
import ru.sentyurin.util.exсeption.NoDataInRepository;

/**
 * Servlet implementation class BooksController
 */
@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String JSON_MIME = "application/json";
	private static final String NOT_FOUND_BY_ID_MSG = "There is no movie with this ID";

	private MovieService movieService;
	private final ObjectMapper objectMapper;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MovieServlet() {
		super();
		objectMapper = new ObjectMapper();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
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
		} catch (IncorrectInputException e) {
			response.setStatus(400);
			response.getWriter().print("Incorrect data: " + e.getMessage());
		} catch (InconsistentInputException e) {
			response.setStatus(400);
			response.getWriter().print("Inconsistent data: " + e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String json = request.getReader().lines().collect(Collectors.joining("\n"));
		try {
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
		} catch (NoDataInRepository e) {
			response.setStatus(404);
			response.getWriter().print(NOT_FOUND_BY_ID_MSG);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String movieIdAsString = request.getParameter("id");
		if (movieIdAsString == null) {
			response.setStatus(400);
			response.getWriter().print("There must be path variable \"id\"");
			return;
		}

		int movieId;
		try {
			movieId = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.setStatus(400);
			response.getWriter().print("Incorrect \"id\" path variable format");
			return;
		}

		boolean resultStatus = movieService.deleteMovie(movieId);
		if (resultStatus) {
			response.setStatus(200);
			response.getWriter().printf("Movie with id %d has been deleted", movieId);
		} else {
			response.setStatus(404);
			response.getWriter().print(NOT_FOUND_BY_ID_MSG);
		}

	}

	@Override
	public void init() throws ServletException {
		super.init();
		movieService = new MovieServiceImpl();
	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}

	private void doGetById(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		int movieId;
		try {
			movieId = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.setStatus(400);
			response.getWriter().print("Incorrect \"id\" path variable format");
			return;
		}

		Optional<MovieOutgoingDto> optionalMovie = movieService.getMovieById(movieId);
		if (optionalMovie.isEmpty()) {
			response.setStatus(404);
			response.getWriter().print(NOT_FOUND_BY_ID_MSG);
			return;
		}

		response.setContentType(JSON_MIME);
		response.getWriter().print(objectMapper.writeValueAsString(optionalMovie.get()));
	}

}
