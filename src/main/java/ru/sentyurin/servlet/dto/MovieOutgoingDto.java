package ru.sentyurin.servlet.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

public class MovieOutgoingDto {

	private Integer id;
	private String title;
	private Integer releaseYear;
	private Integer directorId;
	private String directorName;

	public MovieOutgoingDto() {
	}

	public MovieOutgoingDto(Movie movie) {
		id = movie.getId();
		title = movie.getTitle();
		releaseYear = movie.getReleaseYear();
		Director director = movie.getDirector();
		if (director != null) {
			directorId = director.getId();
			directorName = director.getName();
		}
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

	public MovieOutgoingDto setId(Integer id) {
		this.id = id;
		return this;
	}

	public MovieOutgoingDto setTitle(String title) {
		this.title = title;
		return this;
	}

	public MovieOutgoingDto setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
		return this;
	}

	public MovieOutgoingDto setDirectorId(Integer directorId) {
		this.directorId = directorId;
		return this;
	}

	public MovieOutgoingDto setDirectorName(String directorName) {
		this.directorName = directorName;
		return this;
	}

	public String toJsonRepresentation() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

}
