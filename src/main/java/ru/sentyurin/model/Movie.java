package ru.sentyurin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Movie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private Integer releaseYear;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "director_id")
	private Director director;

	public Movie() {
	}

	public Movie(Integer id, String title, Integer releaseYear, Director director) {
		this.id = id;
		this.title = title;
		this.releaseYear = releaseYear;
		this.director = director;
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Integer getReleaseYear() {
		return releaseYear;
	}

	public Director getDirector() {
		return director;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

	public void setDirector(Director director) {
		this.director = director;
	}

}
