package ru.sentyurin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@RestController
@RequestMapping("/movies")
public class MovieController {
	private static final String NOT_FOUND_BY_ID_MSG = "There is no movie with this ID";

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
	public void doDelete(@PathVariable Integer id) {
		movieService.deleteMovie(id);
	}

}