package ru.sentyurin.service;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;

public interface MovieService {

	MovieOutgoingDto createMovie(MovieIncomingDto movie);

	List<MovieOutgoingDto> getMovies();

	Optional<MovieOutgoingDto> getMovieById(int id);
	
	MovieOutgoingDto updateMovie(MovieIncomingDto movie);

	boolean deleteMovie(int id);

}
