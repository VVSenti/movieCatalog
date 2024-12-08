package ru.sentyurin.servlet.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

public class DirectorOutgoingDto {

	private Integer id;
	private String name;
	private List<MovieOutgoingDto> movies;

	public DirectorOutgoingDto() {
	}

	public DirectorOutgoingDto(Director director) {
		Objects.requireNonNull(director);
		id = director.getId();
		name = director.getName();
		if (director.getMovies() == null) {
			return;
		}
		movies = new ArrayList<>();
		for (Movie movie : director.getMovies()) {
			movies.add(new MovieOutgoingDto(movie));
		}
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<MovieOutgoingDto> getMovies() {
		return movies;
	}

	public DirectorOutgoingDto setId(Integer id) {
		this.id = id;
		return this;
	}

	public DirectorOutgoingDto setName(String name) {
		this.name = name;
		return this;
	}

	public DirectorOutgoingDto setMovies(List<MovieOutgoingDto> movies) {
		this.movies = movies;
		return this;
	}

	public String toJsonRepresentation() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public static void main(String[] args) throws JsonProcessingException {
		Director director = new Director();
		List<Movie> movies = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Movie movie = new Movie();
			movie.setId(i);
			movies.add(movie);
		}
		director.setId(1);
		director.setName("Slava");
		director.setMovies(movies);
		System.out.println(new DirectorOutgoingDto(director).toJsonRepresentation());

	}

}
