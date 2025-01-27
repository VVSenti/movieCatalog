package ru.sentyurin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.service.DirectorService;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

class DirectorControllerTest {
	private DirectorService service;
	private DirectorController controller;

	@BeforeEach
	void init() {
		service = Mockito.mock(DirectorService.class);
		controller = new DirectorController(service);
	}

	@Test
	void shouldReturnAllDirectors() {
		List<DirectorOutgoingDto> directorsOutgoingDtos = List.of(new DirectorOutgoingDto(),
				new DirectorOutgoingDto());
		Mockito.when(service.getDirectors()).thenReturn(directorsOutgoingDtos);
		List<DirectorOutgoingDto> directorsDtos = controller.doGet();
		assertEquals(directorsOutgoingDtos.size(), directorsDtos.size());
		verify(service).getDirectors();
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldReturnDirectorById() {
		Integer directorIdToGet = 2;
		Mockito.when(service.getDirectorById(directorIdToGet))
				.thenReturn(Optional.of(new DirectorOutgoingDto()));
		DirectorOutgoingDto directorDto = controller.doGetById(directorIdToGet);
		assertNotNull(directorDto);
		verify(service).getDirectorById(directorIdToGet);
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldThrowExceptionWhenGetByIdWithInvalidId() {
		Mockito.when(service.getDirectorById(any(Integer.class))).thenReturn(Optional.empty());
		Integer directorIdToGet = 3;
		assertThrows(NoDataInRepositoryException.class,
				() -> controller.doGetById(directorIdToGet));
		verify(service).getDirectorById(directorIdToGet);
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldReturnCorrectStatusWhenDeleteById() {
		Integer directorIdToDelete = 2;
		controller.doDelete(directorIdToDelete);
		verify(service).deleteDirector(directorIdToDelete);
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldWorkCorrectlyWhenPost() {
		Mockito.when(service.createDirector(any(DirectorIncomingDto.class)))
				.thenReturn(new DirectorOutgoingDto());
		ResponseEntity<DirectorOutgoingDto> response = controller.doPost(new DirectorIncomingDto());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(service).createDirector(any(DirectorIncomingDto.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldCorrectlyUpdateDirectorById() {
		Mockito.when(service.updateDirector(Mockito.any(DirectorIncomingDto.class)))
				.thenReturn(new DirectorOutgoingDto());
		DirectorOutgoingDto directorDto = controller.doPut(new DirectorIncomingDto());
		assertNotNull(directorDto);
		verify(service).updateDirector(any(DirectorIncomingDto.class));
		verifyNoMoreInteractions(service);
	}

}