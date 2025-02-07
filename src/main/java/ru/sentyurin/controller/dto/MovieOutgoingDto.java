package ru.sentyurin.controller.dto;

public class MovieOutgoingDto {

	private Integer id;
	private String title;
	private Integer releaseYear;
	private Integer directorId;
	private String directorName;

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
