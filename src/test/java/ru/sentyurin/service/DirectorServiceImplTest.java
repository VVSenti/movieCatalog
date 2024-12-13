package ru.sentyurin.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ru.sentyurin.model.Director;
import ru.sentyurin.repository.DirectorRepository;
import ru.sentyurin.service.impl.DirectorServiceImpl;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;
import ru.sentyurin.servlet.mapper.DirectorDtoMapperImpl;
import ru.sentyurin.util.exception.IncompleateInputExeption;

class DirectorServiceImplTest {

	private DirectorServiceImpl directorService;
	private DirectorRepository directorRepository;
	private DirectorDtoMapperImpl mapper;

	@BeforeEach
	void init() {
		directorRepository = Mockito.mock(DirectorRepository.class);

		directorService = new DirectorServiceImpl();
		directorService.setDirectorRepository(directorRepository);

		mapper = new DirectorDtoMapperImpl();

	}

	@Test
	void shouldCorrectlyCreateDirectorWithName() {
		DirectorIncomingDto incomingDto = new DirectorIncomingDto(1, "Quentin Tarantino");
		Mockito.doReturn(new Director(1, "Quentin Tarantino", null)).when(directorRepository)
				.save(Mockito.any(Director.class));

		DirectorOutgoingDto outgoingDto = directorService.createDirector(incomingDto);
		assertEquals(incomingDto.getName(), outgoingDto.getName());
	}

	@Test
	void shouldCorrectlyFindAllDirectors() {
		List<Director> directorsFromMockRepository = List.of(
				new Director(1, "Quentin Tarantino", null), new Director(2, "Tim Berton", null));
		Mockito.when(directorRepository.findAll()).thenReturn(directorsFromMockRepository);
		List<DirectorOutgoingDto> directors = directorService.getDirectors();
		assertEquals(2, directors.size());
	}

	@Test
	void shouldReturnOptionalEmptyIfRepositoryReturnOptionalEmpty() {
		Mockito.doReturn(Optional.empty()).when(directorRepository).findById(Mockito.anyInt());
		Optional<DirectorOutgoingDto> director = directorService.getDirectorById(0);
		assertTrue(director.isEmpty());
	}

	@Test
	void shouldReturnDirectorById() {
		Mockito.doReturn(Optional.of(new Director(1, "Quentin Tarantino", null)))
				.when(directorRepository).findById(Mockito.anyInt());
		Optional<DirectorOutgoingDto> director = directorService.getDirectorById(1);
		assertTrue(director.isPresent());
		assertEquals("Quentin Tarantino", director.get().getName());
	}

	@Test
	void shouldThrowExceptionIfUpdateWithoutId() {
		DirectorIncomingDto directorToUpdate = new DirectorIncomingDto(null, "Quentin Tarantino");
		assertThrows(IncompleateInputExeption.class,
				() -> directorService.updateDirector(directorToUpdate));
	}
	
	@Test
	void shouldThrowExceptionIfUpdateWithoutName() {
		DirectorIncomingDto directorToUpdate = new DirectorIncomingDto(1, null);
		assertThrows(IncompleateInputExeption.class,
				() -> directorService.updateDirector(directorToUpdate));
	}

	@Test
	void shouldCorrectlyUpdate() {
		DirectorIncomingDto directorToUpdate = new DirectorIncomingDto(1, "Quentin Tarantino");
		Mockito.doReturn(Optional.of(mapper.map(directorToUpdate))).when(directorRepository)
				.update(Mockito.any(Director.class));
		DirectorOutgoingDto director = directorService.updateDirector(directorToUpdate);
		assertEquals(directorToUpdate.getName(), director.getName());
	}

	@Test
	void shouldReturnTheSameBooleanValueAsRepositoryWhenDelete() {
		Mockito.doReturn(true).when(directorRepository).deleteById(Mockito.anyInt());
		assertEquals(true, directorService.deleteDirector(1));
		Mockito.doReturn(false).when(directorRepository).deleteById(Mockito.anyInt());
		assertEquals(false, directorService.deleteDirector(1));
	}
	
	@Test
	void shouldReturnDirectorRepository() {
		assertEquals(directorRepository, directorService.getDirectorRepository());
	}

}
