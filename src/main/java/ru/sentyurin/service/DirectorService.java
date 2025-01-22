package ru.sentyurin.service;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;

public interface DirectorService {

	DirectorOutgoingDto createDirector(DirectorIncomingDto director);

	List<DirectorOutgoingDto> getDirectors();

	Optional<DirectorOutgoingDto> getDirectorById(int id);
	
	DirectorOutgoingDto updateDirector(DirectorIncomingDto director);

	boolean deleteDirector(int id);

}
