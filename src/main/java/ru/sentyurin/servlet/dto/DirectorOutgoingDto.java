package ru.sentyurin.servlet.dto;

import java.util.List;

public class DirectorOutgoingDto {

	private Integer id;
	private String name;
	private List<MovieOutgoingDto> movies;

	public DirectorOutgoingDto() {
	}

	public DirectorOutgoingDto(Integer id, String name, List<MovieOutgoingDto> movies) {
		this.id = id;
		this.name = name;
		this.movies = movies;
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
