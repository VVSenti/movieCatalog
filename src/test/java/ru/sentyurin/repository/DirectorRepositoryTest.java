package ru.sentyurin.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import ru.sentyurin.model.Director;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

class DirectorRepositoryTest {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	private DirectorRepository directorRepository;
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
		connectionManager = new DBConnectionProvider(postgres.getJdbcUrl(), postgres.getUsername(),
				postgres.getPassword());
		directorRepository = (DirectorRepository) RepositoryFactory.getRepository(Director.class, Integer.class);
		RepositoryFactory.setConnectionManager(connectionManager);
	}

	@Test
	void shouldGetDirectors() {
		List<Director> directors = directorRepository.findAll();
		directors.forEach(d -> directorRepository.deleteById(d.getId()));
		directorRepository.save(new Director(null, "Slava", null));
		directorRepository.save(new Director(null, "Vasya", null));
		directors = directorRepository.findAll();
		assertEquals(2, directors.size());
	}

	@Test
	void shouldGetDirectorById() {
		directorRepository.save(new Director(null, "Slava", null));
		directorRepository.save(new Director(null, "Vasya", null));
		List<Director> directors = directorRepository.findAll();
		Director directorToFind = directors.get(0);
		Director foundDirector = directorRepository.findById(directorToFind.getId()).get();
		assertEquals(directorToFind.getId(), foundDirector.getId());
		assertEquals(directorToFind.getName(), foundDirector.getName());
	}

	@Test
	void shouldDeleteDirectorById() {
		directorRepository.save(new Director(1, "Slava", null));
		directorRepository.save(new Director(2, "Vasya", null));
		List<Director> directors = directorRepository.findAll();
		Director directorToDelete = directors.get(0);
		boolean resultStatus = directorRepository.deleteById(directorToDelete.getId());
		Optional<Director> foundDirector = directorRepository.findById(directorToDelete.getId());
		assertTrue(foundDirector.isEmpty());
		assertTrue(resultStatus);
	}

	@Test
	void shouldReturnCorrectStatusIfDeleteWithInvalidId() {
		boolean resultStatus = directorRepository.deleteById(0);
		assertFalse(resultStatus);
	}

	@Test
	void shouldUpdateDirector() {
		directorRepository.save(new Director(1, "Slava", null));
		List<Director> directors = directorRepository.findAll();
		Director directorToUpdate = directors.stream().filter(d -> "Slava".equals(d.getName()))
				.findFirst().get();
		directorToUpdate.setName("Igor");
		directorRepository.update(directorToUpdate);
		Director foundDirector = directorRepository.findById(directorToUpdate.getId()).get();
		assertEquals(directorToUpdate.getName(), foundDirector.getName());
	}

	@Test
	void shouldThrowExceptionIfUpdateWithInvalidId() {
		Director directorToUpdateWithInvalidId = new Director(0, "Slava", null);
		assertThrows(NoDataInRepositoryException.class,
				() -> directorRepository.update(directorToUpdateWithInvalidId));
	}

	@Test
	void shouldReturnConnectionManager() {
		assertEquals(connectionManager, directorRepository.getConnectionManager());
	}

}
