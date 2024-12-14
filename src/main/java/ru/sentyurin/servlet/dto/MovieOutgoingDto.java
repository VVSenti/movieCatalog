package ru.sentyurin.servlet.dto;

import java.util.Objects;

public class MovieOutgoingDto {

	private Integer id;
	private String title;
	private Integer releaseYear;
	private Integer directorId;
	private String directorName;

	public MovieOutgoingDto() {
	}

	public MovieOutgoingDto(Integer id, String title, Integer releaseYear, Integer directorId,
			String directorName) {
		this.id = id;
		this.title = title;
		this.releaseYear = releaseYear;
		this.directorId = directorId;
		this.directorName = directorName;
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

}
