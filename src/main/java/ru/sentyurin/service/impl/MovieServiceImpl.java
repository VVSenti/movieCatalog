package ru.sentyurin.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.controller.mapper.MovieDtoMapper;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.DirectorRepository;
import ru.sentyurin.repository.MovieRepository;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@Service
public class MovieServiceImpl implements MovieService {

	private final MovieRepository movieRepository;
	private final DirectorRepository directorRepository;
	private final MovieDtoMapper dtoMapper;

	@Autowired
	public MovieServiceImpl(MovieRepository movieRepository, DirectorRepository directorRepository,
			MovieDtoMapper movieDtoMapper) {
		this.movieRepository = movieRepository;
		this.directorRepository = directorRepository;
		dtoMapper = movieDtoMapper;
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
	@Transactional
	public MovieOutgoingDto createMovie(MovieIncomingDto movie)
			throws IncompleateInputExeption, IncorrectInputException {
		validateMovieData(movie);
		movie.setId(null);
		return mapToOutgoingDto(movieRepository.save(mapFromIncomingDto(movie)));
	}

	/**
	 * Returns all movie entities in repository
	 */
	@Override
	@Transactional
	public List<MovieOutgoingDto> getMovies() {
		return movieRepository.findAll().stream().map(this::mapToOutgoingDto).toList();
	}

	/**
	 * Returns movie entity with specified ID
	 */
	@Override
	@Transactional
	public Optional<MovieOutgoingDto> getMovieById(Integer id) {
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
	@Transactional
	public MovieOutgoingDto updateMovie(MovieIncomingDto movie)
			throws IncompleateInputExeption, IncorrectInputException {
		if (movie.getId() == null)
			throw new IncompleateInputExeption("There must be a movie ID");
		validateMovieData(movie);
		Movie movieToUpdate = movieRepository.findById(movie.getId()).orElseThrow(
				() -> new NoDataInRepositoryException("There is no movie with such ID"));
		movieToUpdate.setReleaseYear(movie.getReleaseYear());
		movieToUpdate.setTitle(movie.getTitle());
		if (!movieToUpdate.getDirector().getId().equals(movie.getDirectorId())) {
			Director director = directorRepository.findById(movie.getDirectorId()).orElseThrow(
					() -> new NoDataInRepositoryException("There is no director with such ID"));
			movieToUpdate.setDirector(director);
		}
		return mapToOutgoingDto(movieRepository.save(movieToUpdate));
	}

	/**
	 * Deletes movie entity from repository
	 */
	@Override
	@Transactional
	public void deleteMovie(Integer id) {
		movieRepository.deleteById(id);
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
		if (movie.getDirectorId() == null)
			throw new IncompleateInputExeption("There must be director ID");
	}

	private MovieOutgoingDto mapToOutgoingDto(Movie movie) {
		return dtoMapper.map(movie);
	}

	private Movie mapFromIncomingDto(MovieIncomingDto incomingDto) {
		return dtoMapper.map(incomingDto);
	}
}
