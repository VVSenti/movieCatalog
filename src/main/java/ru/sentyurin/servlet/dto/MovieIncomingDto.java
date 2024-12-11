package ru.sentyurin.servlet.dto;

public class MovieIncomingDto {
	private Integer id;
	private String title;
	private Integer releaseYear;
	private Integer directorId;
	private String directorName;

	public MovieIncomingDto() {
	}

	public MovieIncomingDto(Integer id, String title, Integer releaseYear, Integer directorId,
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

	public MovieIncomingDto setTitle(String title) {
		this.title = title;
		return this;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

	public void setDirectorId(Integer directorId) {
		this.directorId = directorId;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}
}
