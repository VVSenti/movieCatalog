package ru.sentyurin.model;

public class Movie {

	private Integer id;
	private String title;
	private Integer releaseYear;
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
