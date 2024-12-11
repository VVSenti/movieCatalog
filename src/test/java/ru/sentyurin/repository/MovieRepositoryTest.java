package ru.sentyurin.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import ru.sentyurin.db.ConnectionManager;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.util.exсeption.InconsistentInputException;
import ru.sentyurin.util.exсeption.IncorrectInputException;
import ru.sentyurin.util.exсeption.NoDataInRepository;

class MovieRepositoryTest {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	private MovieRepository movieRepository;
	private DBConnectionProvider connectionManager;

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@BeforeEach
	void init() {
		connectionManager = new DBConnectionProvider(postgres.getJdbcUrl(),
				postgres.getUsername(), postgres.getPassword());
		movieRepository = (MovieRepository) RepositoryFactory.getRepository(Movie.class);
		RepositoryFactory.setConnectionManager(connectionManager);
	}

	@Test
	void shouldGetMovies() {
		List<Movie> movies = movieRepository.findAll();
		movies.forEach(d -> movieRepository.deleteById(d.getId()));
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		movieRepository.save(new Movie(null, "Pulp Fiction", 1994, director));
		movies = movieRepository.findAll();
		assertEquals(2, movies.size());
	}

	@Test
	void shouldGetMovieById() {
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		movieRepository.save(new Movie(null, "Pulp Fiction", 1994, director));
		List<Movie> movies = movieRepository.findAll();
		Movie movieToFind = movies.getFirst();
		Movie foundDirector = movieRepository.findById(movieToFind.getId()).get();
		assertEquals(movieToFind.getId(), foundDirector.getId());
		assertEquals(movieToFind.getTitle(), foundDirector.getTitle());
		assertEquals(movieToFind.getReleaseYear(), foundDirector.getReleaseYear());
	}

	@Test
	void shouldDeleteMovieById() {
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		movieRepository.save(new Movie(null, "Pulp Fiction", 1994, director));
		List<Movie> movies = movieRepository.findAll();
		Movie movieToDelete = movies.getFirst();
		
		boolean resultStatus = movieRepository.deleteById(movieToDelete.getId());
		Optional<Movie> foundMovie = movieRepository.findById(movieToDelete.getId());
		assertTrue(resultStatus);
		assertTrue(foundMovie.isEmpty());
	}

	@Test
	void shouldReturnCorrectStatusIfDeleteWithInvalidId() {
		boolean resultStatus = movieRepository.deleteById(0);
		assertFalse(resultStatus);
	}

	@Test
	void shouldUpdateMovie() {
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		List<Movie> movies = movieRepository.findAll();
		Movie movieToUpdate = movies.stream().filter(d -> "Reservoir dogs".equals(d.getTitle()))
				.findFirst().get();
		movieToUpdate.setTitle("Django Unchained");
		movieToUpdate.setReleaseYear(2012);
		movieRepository.update(movieToUpdate);
		Movie foundMovie = movieRepository.findById(movieToUpdate.getId()).get();
		assertEquals(movieToUpdate.getTitle(), foundMovie.getTitle());
		assertEquals(movieToUpdate.getReleaseYear(), foundMovie.getReleaseYear());
	}

	@Test
	void shouldThrowExceptionIfUpdateWithInvalidId() {
		Director director = new Director(null, "Quentin Tarantino", null);
		Movie movieToUpdateWithInvalidId = new Movie(0, "Reservoir dogs", 1992, director);
		assertThrows(NoDataInRepository.class,
				() -> movieRepository.update(movieToUpdateWithInvalidId));
	}
	
	@Test
	void shouldThrowExceptionIfSaveWithInconsistentIdOfDirector() {
		List<Movie> movies = movieRepository.findAll();
		movies.forEach(d -> movieRepository.deleteById(d.getId()));
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		
		director.setId(0);
		Movie movieToSave = new Movie(null, "Pulp Fiction", 1994, director);
		
		assertThrows(InconsistentInputException.class,
				() -> movieRepository.save(movieToSave));
	}
	
	@Test
	void shouldThrowExceptionIfSaveWithInvalidIdOfDirector() {
		List<Movie> movies = movieRepository.findAll();
		movies.forEach(d -> movieRepository.deleteById(d.getId()));
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		
		director.setId(0);
		director.setName(null);
		Movie movieToSave = new Movie(null, "Pulp Fiction", 1994, director);
		
		assertThrows(IncorrectInputException.class,
				() -> movieRepository.save(movieToSave));
	}
	
	@Test
	void shouldDeleteMoviesByDirectorId() {
		List<Movie> movies = movieRepository.findAll();
		movies.forEach(d -> movieRepository.deleteById(d.getId()));
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		movieRepository.save(new Movie(null, "Pulp Fiction", 1994, director));
		movies = movieRepository.findAll();
		assertEquals(2, movies.size());
		Integer directorId = movies.getFirst().getDirector().getId();
		movieRepository.deleteByDirectorId(directorId);
		movies = movieRepository.findAll();
		assertEquals(0, movies.size());		
	}
	
	@Test
	void shouldReturnMoviesByDirectorId() {
		List<Movie> movies = movieRepository.findAll();
		movies.forEach(d -> movieRepository.deleteById(d.getId()));
		Director director = new Director(null, "Quentin Tarantino", null);
		movieRepository.save(new Movie(null, "Reservoir dogs", 1992, director));
		movieRepository.save(new Movie(null, "Pulp Fiction", 1994, director));
		movies = movieRepository.findAll();
		assertEquals(2, movies.size());
		Integer directorId = movies.getFirst().getDirector().getId();
		movies = movieRepository.findByDirectorId(directorId);
		assertEquals(2, movies.size());		
	}
	
	@Test
	void shouldReturnConnectionManager() {
		assertEquals(connectionManager, movieRepository.getConnectionManager());
	}
	
	@Test
	void shouldReturnMovieRepository() {
		assertNotNull(movieRepository.getDirectorRepository());
	}

}
