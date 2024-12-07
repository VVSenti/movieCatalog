package ru.sentyurin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;

import ru.sentyurin.model.Movie;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.service.impl.MovieServiceImpl;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.util.exeption.IncompleateInputExeption;
import ru.sentyurin.util.exeption.InconsistentInputException;
import ru.sentyurin.util.exeption.IncorrectInputException;

/**
 * Servlet implementation class BooksController
 */
@WebServlet("/movies")
public class MovieServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private MovieService movieService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MovieServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		if (request.getParameter("id") != null) {
			doGetById(request, response);
			return;
		}

		String jsons = movieService.getMovies().stream().map(t -> {
			try {
				return t.toJsonRepresentation();
			} catch (JsonProcessingException e) {
				return "{JSON processing has failed}";
			}
		}).collect(Collectors.joining(", \n"));

		response.getWriter().print("[" + jsons + "]");
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
			MovieOutgoingDto movie = movieService.createMovie(MovieIncomingDto.from(json));
			response.setContentType("application/json");
			response.getWriter().print(movie.toJsonRepresentation());
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
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	@Override
	public void init() throws ServletException {
		super.init();
		movieService = MovieServiceImpl.getMovieService();
	}

	public MovieService getMovieService() {
		return movieService;
	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}

	private void doGetById(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/json");

		int movieId;
		try {
			movieId = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.setStatus(400);
			return;
		}

		Optional<MovieOutgoingDto> optionalMovie = movieService.getMovieById(movieId);
		if (optionalMovie.isEmpty()) {
			response.setStatus(404);
			return;
		}
		response.getWriter().print(optionalMovie.get().toJsonRepresentation());
	}

}
