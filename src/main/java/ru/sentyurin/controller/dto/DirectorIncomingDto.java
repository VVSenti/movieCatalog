package ru.sentyurin.controller.dto;

public class DirectorIncomingDto {
	private Integer id;
	private String name;

	public DirectorIncomingDto() {
	}

	public DirectorIncomingDto(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public DirectorIncomingDto setId(Integer id) {
		this.id = id;
		return this;
	}

	public DirectorIncomingDto setName(String name) {
		this.name = name;
		return this;
	}
}
