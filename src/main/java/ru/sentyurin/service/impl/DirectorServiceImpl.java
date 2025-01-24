package ru.sentyurin.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.controller.mapper.DirectorDtoMapper;
import ru.sentyurin.dao.Dao;
import ru.sentyurin.model.Director;
import ru.sentyurin.service.DirectorService;
import ru.sentyurin.util.exception.IncompleateInputExeption;

@Service
public class DirectorServiceImpl implements DirectorService {

	private final Dao<Director, Integer> directorRepository;
	private final DirectorDtoMapper dtoMapper;

	@Autowired
	public DirectorServiceImpl(Dao<Director, Integer> directorRepositoryHiber,
			DirectorDtoMapper directorDtoMapper) {
		directorRepository = directorRepositoryHiber;
		dtoMapper = directorDtoMapper;
	}

	/**
	 * Gets a repository of director entities
	 */
	public Dao<Director, Integer> getDirectorRepository() {
		return directorRepository;
	}

	/**
	 * Creates new director entity in repository
	 * 
	 * @throws IncompleateInputExeption if field {@code name} in {@code director} is
	 *                                  {@code null}
	 */
	@Override
	@Transactional
	public DirectorOutgoingDto createDirector(DirectorIncomingDto director)
			throws IncompleateInputExeption {
		directorDataValidation(director);
		return dtoMapper.map(directorRepository.save(dtoMapper.map(director)));
	}

	/**
	 * Returns all director entities in repository
	 */
	@Override
	@Transactional()
	public List<DirectorOutgoingDto> getDirectors() {
		return directorRepository.findAll().stream().map(this::mapToOutgoingDto).toList();
	}

	/**
	 * Returns director entity with specified ID
	 */
	@Override
	@Transactional
	public Optional<DirectorOutgoingDto> getDirectorById(int id) {
		Optional<Director> optionalDirector = directorRepository.findById(id);
		return optionalDirector.isEmpty() ? Optional.empty()
				: Optional.of(mapToOutgoingDto(optionalDirector.get()));
	}

	/**
	 * Creates new director entity in repository
	 * 
	 * @param director contains field {@code directorId} (ID of director entity to
	 *                 update) and new data to persist
	 * 
	 * @throws IncompleateInputExeption if fields {@code id} or {@code name} in
	 *                                  {@code director} is {@code null}
	 */
	@Override
	@Transactional
	public DirectorOutgoingDto updateDirector(DirectorIncomingDto director) {
		if (director.getId() == null)
			throw new IncompleateInputExeption("There must be a director ID");
		directorDataValidation(director);
		Director updatedDirector = directorRepository.update(mapFromIncomingDto(director))
				.orElseThrow();
		return mapToOutgoingDto(updatedDirector);
	}

	/**
	 * Deletes director entity from repository
	 */
	@Override
	@Transactional
	public boolean deleteDirector(int id) {
		return directorRepository.deleteById(id);
	}

	private void directorDataValidation(DirectorIncomingDto director)
			throws IncompleateInputExeption {
		if (director.getName() == null)
			throw new IncompleateInputExeption("There must be a director name");
	}

	private DirectorOutgoingDto mapToOutgoingDto(Director director) {
		return dtoMapper.map(director);
	}

	private Director mapFromIncomingDto(DirectorIncomingDto incomingDto) {
		return dtoMapper.map(incomingDto);
	}
}
