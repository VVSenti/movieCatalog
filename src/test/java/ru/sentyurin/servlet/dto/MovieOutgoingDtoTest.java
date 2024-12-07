package ru.sentyurin.servlet.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

class MovieOutgoingDtoTest {
	
	private static Movie movie;
	private static Director director;
	private static Map<String, Object> fieldValues;
	
	@BeforeAll
	static void init() {
		fieldValues = new HashMap<>();
		fieldValues.put("id", 1);
		fieldValues.put("title", "Reservoir Dogs");
		fieldValues.put("releaseYear", 1992);
		fieldValues.put("directorId", 2);
		fieldValues.put("directorName", "Quentin Tarantino");

		director = new Director((Integer) fieldValues.get("directorId"),
				(String) fieldValues.get("directorName"), null);
		movie = new Movie((Integer) fieldValues.get("id"), (String) fieldValues.get("title"),
				(Integer) fieldValues.get("releaseYear"), director);
	}
	

	@Test
	void shouldReturnCorrectJson() throws JsonProcessingException {
		MovieOutgoingDto movieOutgoingDto = new MovieOutgoingDto(movie);
		String json = movieOutgoingDto.toJsonRepresentation();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> fieldValuesFromJson = mapper.readValue(json,
				new TypeReference<Map<String, Object>>() {});
		
		for(String field : fieldValues.keySet()) {
			assertEquals(fieldValues.get(field), fieldValuesFromJson.get(field));
		}
	}
	
	@Test
	void shouldCorrectlyConvertMovieToDto() {
		MovieOutgoingDto movieDto = new MovieOutgoingDto(movie);
		assertEquals(fieldValues.get("id"), movieDto.getId());
		assertEquals(fieldValues.get("title"), movieDto.getTitle());
		assertEquals(fieldValues.get("releaseYear"), movieDto.getReleaseYear());
		assertEquals(fieldValues.get("directorId"), movieDto.getDirectorId());
		assertEquals(fieldValues.get("directorName"), movieDto.getDirectorName());
	}
	
	@Test
	void settersShouldWorkCorrectly() {
		MovieOutgoingDto movieDto = new MovieOutgoingDto();
		movieDto.setId((Integer) fieldValues.get("id"));
		movieDto.setTitle((String) fieldValues.get("title"));
		movieDto.setReleaseYear((Integer) fieldValues.get("releaseYear"));
		movieDto.setDirectorId((Integer) fieldValues.get("directorId"));
		movieDto.setDirectorName((String) fieldValues.get("directorName"));
		
		assertEquals(fieldValues.get("id"), movieDto.getId());
		assertEquals(fieldValues.get("title"), movieDto.getTitle());
		assertEquals(fieldValues.get("releaseYear"), movieDto.getReleaseYear());
		assertEquals(fieldValues.get("directorId"), movieDto.getDirectorId());
		assertEquals(fieldValues.get("directorName"), movieDto.getDirectorName());
	}
	
	@Test
	void constructorShouldCorrectlyWorkWithEmptyFields() {
		MovieOutgoingDto movieDto = new MovieOutgoingDto(new Movie());
		assertEquals(null, movieDto.getId());
		assertEquals(null, movieDto.getTitle());
		assertEquals(null, movieDto.getReleaseYear());
		assertEquals(null, movieDto.getDirectorId());
		assertEquals(null, movieDto.getDirectorName());
	}

}
