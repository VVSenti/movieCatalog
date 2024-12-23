package ru.sentyurin.service.impl;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.Repository;
import ru.sentyurin.repository.RepositoryFactory;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.servlet.mapper.MovieDtoMapper;
import ru.sentyurin.servlet.mapper.MovieDtoMapperImpl;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.IncorrectInputException;

public class MovieServiceImpl implements MovieService {

	private Repository<Movie, Integer> movieRepository;
	private final MovieDtoMapper dtoMapper;

	public MovieServiceImpl() {
		movieRepository = RepositoryFactory.getRepository(Movie.class, Integer.class);
		dtoMapper = new MovieDtoMapperImpl();
	}

	/**
	 * Gets a repository of movie entities
	 */
	public Repository<Movie, Integer> getMovieRepository() {
		return movieRepository;
	}

	/**
	 * Sets a repository of movie entities
	 * 
	 * @param movieRepository
	 */
	public void setMovieRepository(Repository<Movie, Integer> movieRepository) {
		this.movieRepository = movieRepository;
	}

	/**
	 * Creates new movie entity in repository
	 * 
	 * @throws IncompleateInputExeption if fields {@code title}, or
	 *                                  {@code releaseYear} in {@code movie} is
	 *                                  {@code null}
	 * 
	 * @throws IncompleateInputExeption if fields no {@code directorId} and
	 *                                  {@code directorName} in {@code movie} are
	 *                                  {@code null}
	 * @throws IncorrectInputException  if field {@code releaseYear} in
	 *                                  {@code movie} less than 1895
	 */
	@Override
	public MovieOutgoingDto createMovie(MovieIncomingDto movie)
			throws IncompleateInputExeption, IncorrectInputException {
		validateMovieData(movie);
		return mapToOutgoingDto(movieRepository.save(mapFromIncomingDto(movie)));
	}

	/**
	 * Returns all movie entities in repository
	 */
	@Override
	public List<MovieOutgoingDto> getMovies() {
		return movieRepository.findAll().stream().map(this::mapToOutgoingDto).toList();
	}

	/**
	 * Returns movie entity with specified ID
	 */
	@Override
	public Optional<MovieOutgoingDto> getMovieById(int id) {
		Optional<Movie> optionalMovie = movieRepository.findById(id);
		return optionalMovie.isEmpty() ? Optional.empty()
				: Optional.of(mapToOutgoingDto(optionalMovie.get()));
	}

	/**
	 * Updates movie entity in repository
	 * 
	 * @param movie contains field {@code movieId} (ID of movie entity to update)
	 *              and new data to persist
	 * 
	 * @throws IncompleateInputExeption if fields {@code movieId}, {@code title}, or
	 *                                  {@code releaseYear} in {@code movie} is
	 *                                  {@code null}
	 * 
	 * @throws IncompleateInputExeption if fields no {@code directorId} and
	 *                                  {@code directorName} in {@code movie} are
	 *                                  {@code null}
	 * @throws IncorrectInputException  if field {@code releaseYear} in
	 *                                  {@code movie} less than 1895
	 */
	@Override
	public MovieOutgoingDto updateMovie(MovieIncomingDto movie)
			throws IncompleateInputExeption, IncorrectInputException {
		if (movie.getId() == null)
			throw new IncompleateInputExeption("There must be a movie ID");
		validateMovieData(movie);
		return mapToOutgoingDto(movieRepository.update(mapFromIncomingDto(movie)).orElseThrow());
	}

	/**
	 * Deletes movie entity from repository
	 */
	@Override
	public boolean deleteMovie(int id) {
		return movieRepository.deleteById(id);
	}

	private void validateMovieData(MovieIncomingDto movie)
			throws IncompleateInputExeption, IncorrectInputException {
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

	private MovieOutgoingDto mapToOutgoingDto(Movie movie) {
		return dtoMapper.map(movie);
	}

	private Movie mapFromIncomingDto(MovieIncomingDto incomingDto) {
		return dtoMapper.map(incomingDto);
	}
}
