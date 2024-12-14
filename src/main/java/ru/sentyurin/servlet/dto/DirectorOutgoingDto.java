package ru.sentyurin.servlet.dto;

import java.util.List;

public class DirectorOutgoingDto {

	private Integer id;
	private String name;
	private List<MovieOutgoingDto> movies;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<MovieOutgoingDto> getMovies() {
		return movies;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMovies(List<MovieOutgoingDto> movies) {
		this.movies = movies;
	}
}
