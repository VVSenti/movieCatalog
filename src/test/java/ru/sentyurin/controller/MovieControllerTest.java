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

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

class MovieControllerTest {
	private MovieService service;
	private MovieController controller;

	@BeforeEach
	void init() {
		service = Mockito.mock(MovieService.class);
		controller = new MovieController(service);
	}

	@Test
	void shouldReturnAllMovies() {
		List<MovieOutgoingDto> moviesOutgoingDtos = List.of(new MovieOutgoingDto(),
				new MovieOutgoingDto());
		Mockito.when(service.getMovies()).thenReturn(moviesOutgoingDtos);
		List<MovieOutgoingDto> movieDtos = controller.doGet();
		assertEquals(moviesOutgoingDtos.size(), movieDtos.size());
		verify(service).getMovies();
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldReturnMovieById() {
		Integer movieIdToGet = 2;
		Mockito.when(service.getMovieById(movieIdToGet))
				.thenReturn(Optional.of(new MovieOutgoingDto()));
		MovieOutgoingDto movieDto = controller.doGetById(movieIdToGet);
		assertNotNull(movieDto);
		verify(service).getMovieById(movieIdToGet);
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldThrowExceptionWhenGetWithInvalidId() {
		Integer movieIdToGet = 2;
		Mockito.when(service.getMovieById(movieIdToGet)).thenReturn(Optional.empty());
		assertThrows(NoDataInRepositoryException.class, () -> controller.doGetById(movieIdToGet));
		verify(service).getMovieById(movieIdToGet);
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldDeleteById() {
		Integer movieIdToDelete = 3;
		controller.doDelete(movieIdToDelete);
		verify(service).deleteMovie(movieIdToDelete);
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldPost() {
		Mockito.when(service.createMovie(any(MovieIncomingDto.class)))
				.thenReturn(new MovieOutgoingDto());
		ResponseEntity<MovieOutgoingDto> response = controller.doPost(new MovieIncomingDto());
		assertNotNull(response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(service).createMovie(any(MovieIncomingDto.class));
		verifyNoMoreInteractions(service);
	}

	@Test
	void shouldUpdateMovie() {
		Mockito.when(service.updateMovie(any(MovieIncomingDto.class)))
				.thenReturn(new MovieOutgoingDto());
		MovieOutgoingDto movieDto = controller.doPut(new MovieIncomingDto());
		assertNotNull(movieDto);
		verify(service).updateMovie(any(MovieIncomingDto.class));
		verifyNoMoreInteractions(service);
	}

}
