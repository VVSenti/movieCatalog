package ru.sentyurin.servlet.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

public class MovieIncomingDto {
	private Integer id;
	private String title;
	private Integer releaseYear;
	private Integer directorId;
	private String directorName;

	/**
	 * makes MovieIncomingDto instance from JSON string
	 * 
	 * @param json string
	 * @return
	 * @throws JsonProcessingException
	 */
	public static MovieIncomingDto from(String json) throws JsonProcessingException {
		return new ObjectMapper().readValue(json, MovieIncomingDto.class);
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

	public Integer getDirectorId() {
		return directorId;
	}

	public String getDirectorName() {
		return directorName;
	}

	public MovieIncomingDto setTitle(String title) {
		this.title = title;
		return this;
	}

	public MovieIncomingDto setId(Integer id) {
		this.id = id;
		return this;
	}

	public MovieIncomingDto setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
		return this;
	}

	public MovieIncomingDto setDirectorId(Integer directorId) {
		this.directorId = directorId;
		return this;
	}

	public MovieIncomingDto setDirectorName(String directorName) {
		this.directorName = directorName;
		return this;
	}
	
	public Movie toMovie() {
		Director director = new Director();
		director.setId(directorId);
		director.setName(directorName);
		Movie movie = new Movie();
		movie.setId(id);
		movie.setTitle(title);
		movie.setReleaseYear(releaseYear);
		movie.setDirector(director);
		return movie;
	}
	
}
