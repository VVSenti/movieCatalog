package ru.sentyurin.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.controller.mapper.MovieDtoMapper;
import ru.sentyurin.controller.mapper.MovieDtoMapperImpl;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.DirectorRepository;
import ru.sentyurin.repository.MovieRepository;
import ru.sentyurin.service.impl.MovieServiceImpl;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.IncorrectInputException;

class MovieServiceImplTest {

	private MovieServiceImpl movieService;
	private MovieRepository movieRepository;
	private DirectorRepository directorRepository;
	private MovieDtoMapperImpl mapper;
	private MovieDtoMapper dtoMapper;

	@BeforeEach
	void init() {
		movieRepository = Mockito.mock(MovieRepository.class);
		directorRepository = Mockito.mock(DirectorRepository.class);
		dtoMapper = new MovieDtoMapperImpl();
		movieService = new MovieServiceImpl(movieRepository, directorRepository, dtoMapper);
		mapper = new MovieDtoMapperImpl();
	}

	@Test
	void shouldCorrectlyCreateMovie() {
		MovieIncomingDto incomingDto = new MovieIncomingDto(1, "Reservoir dogs", 1992, 1,
				"Quentin Tarantino");
		Mockito.doReturn(
				new Movie(1, "Reservoir dogs", 1992, new Director(1, "Quentin Tarantino", null)))
				.when(movieRepository).save(Mockito.any(Movie.class));

		MovieOutgoingDto outgoingDto = movieService.createMovie(incomingDto);
		assertEquals(incomingDto.getTitle(), outgoingDto.getTitle());
		assertEquals(incomingDto.getReleaseYear(), outgoingDto.getReleaseYear());
		assertEquals(incomingDto.getDirectorId(), outgoingDto.getDirectorId());
		assertEquals(incomingDto.getDirectorName(), outgoingDto.getDirectorName());
	}

	@Test
	void shouldCorrectlyFindAllMovies() {
		Director director = new Director(1, "Quentin Tarantino", null);
		List<Movie> moviesFromMockRepository = List.of(new Movie(1, "RD", 1992, director),
				new Movie(2, "DU", 2012, director));
		Mockito.when(movieRepository.findAll()).thenReturn(moviesFromMockRepository);
		List<MovieOutgoingDto> movies = movieService.getMovies();
		assertEquals(2, movies.size());
	}

	@Test
	void shouldReturnOptionalEmptyIfRepositoryReturnOptionalEmpty() {
		Mockito.doReturn(Optional.empty()).when(movieRepository).findById(Mockito.anyInt());
		Optional<MovieOutgoingDto> movie = movieService.getMovieById(0);
		assertTrue(movie.isEmpty());
	}

	@Test
	void shouldReturnMovieById() {
		Mockito.doReturn(Optional.of(new Movie(1, "RD", 1992, null))).when(movieRepository)
				.findById(Mockito.anyInt());
		Optional<MovieOutgoingDto> movie = movieService.getMovieById(1);
		assertTrue(movie.isPresent());
		assertEquals("RD", movie.get().getTitle());
	}

	@Test
	void shouldThrowExceptionIfUpdateWithoutId() {
		MovieIncomingDto movieToUpdate = new MovieIncomingDto(null, "RD", 2012, 1, "QT");
		assertThrows(IncompleateInputExeption.class, () -> movieService.updateMovie(movieToUpdate));
	}

	@Test
	void shouldThrowExceptionIfUpdateWithoutTitle() {
		MovieIncomingDto movieToUpdate = new MovieIncomingDto(1, null, 2012, 1, "QT");
		assertThrows(IncompleateInputExeption.class, () -> movieService.updateMovie(movieToUpdate));
	}

	@Test
	void shouldThrowExceptionIfUpdateWithoutReleaseYear() {
		MovieIncomingDto movieToUpdate = new MovieIncomingDto(1, "RD", null, 1, "QT");
		assertThrows(IncompleateInputExeption.class, () -> movieService.updateMovie(movieToUpdate));
	}

	@Test
	void shouldThrowExceptionIfUpdateWithInvalidReleaseYear() {
		MovieIncomingDto movieToUpdate = new MovieIncomingDto(1, "RD", 1000, 1, "QT");
		assertThrows(IncorrectInputException.class, () -> movieService.updateMovie(movieToUpdate));
	}

	@Test
	void shouldThrowExceptionIfUpdateWithoutDirectorIdAndTheirName() {
		MovieIncomingDto movieToUpdate = new MovieIncomingDto(1, "RD", 1992, null, null);
		assertThrows(IncompleateInputExeption.class, () -> movieService.updateMovie(movieToUpdate));
	}
	
	@Test
	void shouldDelete() {
		Integer movieId = 7;
		movieService.deleteMovie(movieId);
		verify(movieRepository).deleteById(movieId);
		verifyNoMoreInteractions(movieRepository);
	}

	@Test
	void shouldCorrectlyUpdate() {
		MovieIncomingDto movieToUpdate = new MovieIncomingDto(1, "RD", 1992, 1, "QT");
		Mockito.doReturn(mapper.map(movieToUpdate)).when(movieRepository)
				.save(Mockito.any(Movie.class));
		Mockito.doReturn(Optional.of(mapper.map(movieToUpdate))).when(movieRepository)
				.findById(Mockito.any(Integer.class));
		MovieOutgoingDto movie = movieService.updateMovie(movieToUpdate);
		assertEquals(movieToUpdate.getTitle(), movie.getTitle());
	}

}
