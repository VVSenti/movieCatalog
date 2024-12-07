package ru.sentyurin.servlet.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MovieIncomingDtoTest {

	@Test
	void shouldReturnCorrectJson() {
		MovieIncomingDto movie = new MovieIncomingDto();
		movie.setTitle("Reservoir Dogs").setReleaseYear(1992).setDirectorName("Quentin Tarantino");
	}

}
