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

import ru.sentyurin.service.MovieService;
import ru.sentyurin.service.impl.MovieServiceImpl;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.util.exeption.IncompleateInputExeption;
import ru.sentyurin.util.exeption.InconsistentInputException;
import ru.sentyurin.util.exeption.IncorrectInputException;
import ru.sentyurin.util.exeption.NoDataInRepository;

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

		response.setContentType("application/json");
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
		String json = request.getReader().lines().collect(Collectors.joining("\n"));
		try {
			MovieOutgoingDto movie = movieService.updateMovie(MovieIncomingDto.from(json));
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
		} catch (NoDataInRepository e) {
			response.setStatus(404);
			response.getWriter().print("There is no movie with this ID");
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
			response.getWriter().printf("Movie with id %d has been deleted", movieId);
		} else {
			response.setStatus(404);
			response.getWriter().print("There is no movie with this ID");
		}

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
			response.getWriter().print("There is no movie with this ID");
			return;
		}
		response.setContentType("application/json");
		response.getWriter().print(optionalMovie.get().toJsonRepresentation());
	}

}
