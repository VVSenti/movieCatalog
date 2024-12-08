package ru.sentyurin.servlet.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sentyurin.model.Director;

public class DirectorIncomingDto {
	private Integer id;
	private String name;

	/**
	 * makes DirectorIncomingDto instance from JSON string
	 * 
	 * @param json string
	 * @return
	 * @throws JsonProcessingException
	 */
	public static DirectorIncomingDto from(String json) throws JsonProcessingException {
		return new ObjectMapper().readValue(json, DirectorIncomingDto.class);
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

	public Director toDirector() {
		Director director = new Director();
		director.setId(id);
		director.setName(name);
		return director;
	}

}
