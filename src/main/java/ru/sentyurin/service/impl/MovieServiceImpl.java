package ru.sentyurin.service.impl;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.MovieRepository;
import ru.sentyurin.repository.Repository;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.util.exeption.IncompleateInputExeption;
import ru.sentyurin.util.exeption.IncorrectInputException;
import ru.sentyurin.util.exeption.NoDataInRepository;

public class MovieServiceImpl implements MovieService {

	private static MovieServiceImpl movieService;
	private Repository<Movie, Integer> movieRepository;

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
	public MovieOutgoingDto createMovie(MovieIncomingDto movie)
			throws IncompleateInputExeption, IncorrectInputException {
		movieDataValidation(movie);
		return new MovieOutgoingDto(movieRepository.save(movie.toMovie()));
	}

	@Override
	public List<MovieOutgoingDto> getMovies() {
		return movieRepository.findAll().stream().map(MovieOutgoingDto::new).toList();
	}

	@Override
	public Optional<MovieOutgoingDto> getMovieById(int id) {
		Optional<Movie> optionalMovie = movieRepository.findById(id);
		return optionalMovie.isEmpty() ? Optional.empty()
				: Optional.of(new MovieOutgoingDto(optionalMovie.get()));
//		if (optionalMovie.isEmpty())
//			return Optional.empty();
//		return Optional.of(new MovieOutgoingDto(optionalMovie.get()));
	}

	@Override
	public MovieOutgoingDto updateMovie(MovieIncomingDto movie) {
		if (movie.getId() == null)
			throw new IncompleateInputExeption("There must be a movie ID");
		movieDataValidation(movie);
		return new MovieOutgoingDto(movieRepository.update(movie.toMovie()));
	}

	@Override
	public boolean deleteMovie(int id) {
		return movieRepository.deleteById(id);
	}

	private void movieDataValidation(MovieIncomingDto movie) {
		if (movie.getTitle() == null)
			throw new IncompleateInputExeption("There must be a movie title");
		if (movie.getReleaseYear() == null)
			throw new IncompleateInputExeption("There must be a release year");
		if (movie.getReleaseYear() < 1895)
			throw new IncorrectInputException(
					"A release year is less than 1895. It is unacceptably suspicious");
		if (movie.getDirectorId() == null && movie.getDirectorName() == null)
			throw new IncompleateInputExeption("There must be director ID or their name");
	}

}
