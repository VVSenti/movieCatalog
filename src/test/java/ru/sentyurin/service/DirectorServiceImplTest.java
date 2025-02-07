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

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.controller.mapper.DirectorDtoMapper;
import ru.sentyurin.controller.mapper.DirectorDtoMapperImpl;
import ru.sentyurin.model.Director;
import ru.sentyurin.repository.DirectorRepository;
import ru.sentyurin.repository.MovieRepository;
import ru.sentyurin.service.impl.DirectorServiceImpl;
import ru.sentyurin.util.exception.IncompleateInputExeption;

class DirectorServiceImplTest {

	private DirectorRepository directorRepository;
	private MovieRepository movieRepository;
	private DirectorDtoMapper dtoMapper;
	private DirectorServiceImpl directorService;
	private DirectorDtoMapperImpl mapper;

	@BeforeEach
	void init() {
		directorRepository = Mockito.mock(DirectorRepository.class);
		movieRepository = Mockito.mock(MovieRepository.class);
		dtoMapper = new DirectorDtoMapperImpl();
		directorService = new DirectorServiceImpl(directorRepository, movieRepository, dtoMapper);
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
		Mockito.doReturn(mapper.map(directorToUpdate)).when(directorRepository)
				.save(Mockito.any(Director.class));
		Mockito.doReturn(Optional.of(mapper.map(directorToUpdate))).when(directorRepository)
				.findById(Mockito.any(Integer.class));
		DirectorOutgoingDto director = directorService.updateDirector(directorToUpdate);
		assertEquals(directorToUpdate.getName(), director.getName());
	}

	@Test
	void shouldDelete() {
		Integer directorId = 7;
		directorService.deleteDirector(directorId);
		verify(movieRepository).deleteByDirectorId(directorId);
		verifyNoMoreInteractions(movieRepository);
		verify(directorRepository).deleteById(directorId);
		verifyNoMoreInteractions(directorRepository);
	}

}
