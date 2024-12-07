package ru.sentyurin.servlet.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

}
