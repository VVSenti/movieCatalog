package ru.sentyurin.servlet.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.controller.mapper.MovieDtoMapper;
import ru.sentyurin.controller.mapper.MovieDtoMapperImpl;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

class MovieDtoMapperImplTest {
	
	private static Map<String, Object> fieldValues;
	
	private static MovieDtoMapper dtoMapper;
	
	@BeforeAll
	static void init() {
		fieldValues = new HashMap<>();
		fieldValues.put("id", 1);
		fieldValues.put("title", "Reservoir Dogs");
		fieldValues.put("releaseYear", 1992);
		fieldValues.put("directorId", 2);
		fieldValues.put("directorName", "Quentin Tarantino");
		
		dtoMapper = new MovieDtoMapperImpl();
	}

	@Test
	void shouldCorrectlyMapToOutgoingDto() {
		Director director = new Director((Integer) fieldValues.get("directorId"),
				(String) fieldValues.get("directorName"), null);
		Movie movie = new Movie((Integer) fieldValues.get("id"), (String) fieldValues.get("title"),
				(Integer) fieldValues.get("releaseYear"), director);
		MovieOutgoingDto movieDto = dtoMapper.map(movie);
		assertEquals(fieldValues.get("id"), movieDto.getId());
		assertEquals(fieldValues.get("title"), movieDto.getTitle());
		assertEquals(fieldValues.get("releaseYear"), movieDto.getReleaseYear());
		assertEquals(fieldValues.get("directorId"), movieDto.getDirectorId());
		assertEquals(fieldValues.get("directorName"), movieDto.getDirectorName());
	}
	
	@Test
	void shouldCorrectlyMapToOutgoingDtoWithoutDirectorField() {
		Movie movie = new Movie();
		movie.setId((Integer) fieldValues.get("id"));
		movie.setTitle((String) fieldValues.get("title"));
		movie.setReleaseYear((Integer) fieldValues.get("releaseYear"));
		MovieOutgoingDto movieDto = dtoMapper.map(movie);
		assertEquals(fieldValues.get("id"), movieDto.getId());
		assertEquals(fieldValues.get("title"), movieDto.getTitle());
		assertEquals(fieldValues.get("releaseYear"), movieDto.getReleaseYear());
		assertNull(movieDto.getDirectorId());
		assertNull(movieDto.getDirectorName());
	}
	
	@Test
	void shouldCorrectlyMapFromIncomingDto() {
		MovieIncomingDto incomingDto = new MovieIncomingDto();
		incomingDto.setId((Integer) fieldValues.get("id"));
		incomingDto.setTitle((String) fieldValues.get("title"));
		incomingDto.setReleaseYear((Integer) fieldValues.get("releaseYear"));
		incomingDto.setDirectorId((Integer) fieldValues.get("directorId"));
		incomingDto.setDirectorName((String) fieldValues.get("directorName"));
		
		Movie movieFromDto = dtoMapper.map(incomingDto);
		assertEquals(fieldValues.get("id"), movieFromDto.getId());
		assertEquals(fieldValues.get("title"), movieFromDto.getTitle());
		assertEquals(fieldValues.get("releaseYear"), movieFromDto.getReleaseYear());
		assertEquals(fieldValues.get("directorId"), movieFromDto.getDirector().getId());
		assertEquals(fieldValues.get("directorName"), movieFromDto.getDirector().getName());
	}
	
	@Test
	void shouldCorrectlyMapFromIncompleateIncomingDto() {
		MovieIncomingDto incomingDto = new MovieIncomingDto();
		Movie movieFromDto = dtoMapper.map(incomingDto);
		assertNull(movieFromDto.getId());
		assertNull(movieFromDto.getTitle());
		assertNull(movieFromDto.getReleaseYear());
		assertNotNull(movieFromDto.getDirector());
		assertNull(movieFromDto.getDirector().getId());
		assertNull(movieFromDto.getDirector().getName());
	}

}
