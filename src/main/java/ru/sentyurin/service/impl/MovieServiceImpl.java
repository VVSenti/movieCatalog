package ru.sentyurin.service.impl;

import java.util.List;

import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.MovieRepository;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;

public class MovieServiceImpl implements MovieService {

	private static MovieServiceImpl movieService;
	private MovieRepository movieRepository;

	private MovieServiceImpl() {
		movieRepository = new MovieRepository();
	}

	public static MovieServiceImpl getMovieService() {
		if (movieService == null) {
			movieService = new MovieServiceImpl();
		}
		return movieService;
	}

	@Override
	public void createMovie(MovieIncomingDto movie) {

	}

	@Override
	public List<MovieOutgoingDto> getMovies() {
		return movieRepository.findAll().stream().map(MovieOutgoingDto::new).toList();
	}

	@Override
	public MovieOutgoingDto geMovieById(int id) {
		return null;
	}

	@Override
	public void updateMovie(MovieIncomingDto movie) {

	}

	@Override
	public void deleteMovie(MovieIncomingDto movie) {

	}

}
