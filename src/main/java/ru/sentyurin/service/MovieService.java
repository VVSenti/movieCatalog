package ru.sentyurin.service;

import java.util.List;

import ru.sentyurin.model.Movie;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;

public interface MovieService {

	void createMovie(MovieIncomingDto movie);

	List<MovieOutgoingDto> getMovies();

	MovieOutgoingDto geMovieById(int id);
	
	void updateMovie(MovieIncomingDto movie);

	void deleteMovie(MovieIncomingDto movie);

}
