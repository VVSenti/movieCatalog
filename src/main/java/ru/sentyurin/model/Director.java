package ru.sentyurin.model;

import java.util.List;

public class Director {

	private Integer id;
	private String name;
	private List<Movie> movies;

	public Director() {
	}

	public Director(Integer id, String name, List<Movie> movies) {
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

	public List<Movie> getMovies() {
		return movies;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}

}
