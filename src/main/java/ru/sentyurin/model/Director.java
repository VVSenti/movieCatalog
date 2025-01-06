package ru.sentyurin.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Director {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "director_id")
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
