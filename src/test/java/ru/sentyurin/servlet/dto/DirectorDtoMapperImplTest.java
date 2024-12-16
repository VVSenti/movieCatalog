package ru.sentyurin.servlet.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.servlet.mapper.DirectorDtoMapperImpl;

class DirectorDtoMapperImplTest {

	private static Map<String, Object> fieldValues;

	private static DirectorDtoMapperImpl dtoMapper;
	
	private static final Integer DIRECTOR_ID = 1;
	private static final String DIRECTOR_NAME = "Quentin Tarantino";
	private static final Integer MOVIE_ID = 5;
	private static final String MOVIE_TITLE = "Reservoir dogs";
	private static final Integer MOVIE_RELEASE_YEAR = 1992;
	

	@BeforeAll
	static void init() {
		fieldValues = new HashMap<>();
		fieldValues.put("id", 1);
		fieldValues.put("name", "Quentin Tarantino");

		dtoMapper = new DirectorDtoMapperImpl();
	}

	@Test
	void shouldCorrectlyMapToOutgoingDto() {
		Director director = new Director(DIRECTOR_ID, DIRECTOR_NAME, null);
		Movie movie = new Movie(MOVIE_ID, MOVIE_TITLE, MOVIE_RELEASE_YEAR, director);
		director.setMovies(List.of(movie));

		DirectorOutgoingDto directorDto = dtoMapper.map(director);
		assertEquals(DIRECTOR_ID, directorDto.getId());
		assertEquals(DIRECTOR_NAME, directorDto.getName());
		assertEquals(MOVIE_ID, directorDto.getMovies().get(0).getId());
		assertEquals(MOVIE_TITLE, directorDto.getMovies().get(0).getTitle());
		assertEquals(MOVIE_RELEASE_YEAR, directorDto.getMovies().get(0).getReleaseYear());
	}

	@Test
	void shouldCorrectlyMapToOutgoingDtoWithoutMovies() {
		Director director = new Director(DIRECTOR_ID, DIRECTOR_NAME, null);

		DirectorOutgoingDto directorDto = dtoMapper.map(director);
		assertEquals(DIRECTOR_ID, directorDto.getId());
		assertEquals(DIRECTOR_NAME, directorDto.getName());
		assertNull(directorDto.getMovies());
	}

	@Test
	void shouldCorrectlyMapFromIncomingDto() {
		DirectorIncomingDto incomingDto = new DirectorIncomingDto();
		incomingDto.setId(DIRECTOR_ID);
		incomingDto.setName(DIRECTOR_NAME);
		
		Director directorFromDto = dtoMapper.map(incomingDto);
		assertEquals(DIRECTOR_ID, directorFromDto.getId());
		assertEquals(DIRECTOR_NAME, directorFromDto.getName());
	}

}
