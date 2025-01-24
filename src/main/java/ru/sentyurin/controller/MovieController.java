package ru.sentyurin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonProcessingException;

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.util.exception.DataBaseException;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.InconsistentInputException;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@RestController
@RequestMapping("/movies")
public class MovieController {
	private static final String NOT_FOUND_BY_ID_MSG = "There is no movie with this ID";
	private static final String MOVIE_DELETED_TEMPLATE = "Movie with id %d has been deleted";

	private final MovieService movieService;

	@Autowired
	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}

	@GetMapping
	public List<MovieOutgoingDto> doGet() {
		return movieService.getMovies();
	}

	@GetMapping("/{id}")
	public MovieOutgoingDto doGetById(@PathVariable Integer id) {
		return movieService.getMovieById(id)
				.orElseThrow(() -> new NoDataInRepositoryException(NOT_FOUND_BY_ID_MSG));
	}

	@PostMapping
	public ResponseEntity<MovieOutgoingDto> doPost(@RequestBody MovieIncomingDto input) {
		return new ResponseEntity<>(movieService.createMovie(input), HttpStatus.CREATED);
	}

	@PutMapping
	public MovieOutgoingDto doPut(@RequestBody MovieIncomingDto movieIncomingDto) {
		return movieService.updateMovie(movieIncomingDto);
	}

	@DeleteMapping("/{id}")
	public String doDelete(@PathVariable Integer id) {
		if (movieService.deleteMovie(id)) {
			return String.format(MOVIE_DELETED_TEMPLATE, id);
		} else {
			throw new NoDataInRepositoryException(NOT_FOUND_BY_ID_MSG);
		}
	}

	@ExceptionHandler
	private ResponseEntity<String> handleDatabaseException(DataBaseException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleNotFoundException(NoDataInRepositoryException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleJsonProcessingException(
			JsonProcessingException exception) {
		return new ResponseEntity<>("Bad input JSON", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleIncompleateInputExeption(
			IncompleateInputExeption exeption) {
		return new ResponseEntity<>("Incompleate data: " + exeption.getMessage(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException exception) {
		return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleInconsistentInputException(
			InconsistentInputException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleIncorrectInputException(
			IncorrectInputException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

}