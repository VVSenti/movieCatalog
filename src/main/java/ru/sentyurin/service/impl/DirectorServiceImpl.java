package ru.sentyurin.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.controller.mapper.DirectorDtoMapper;
import ru.sentyurin.model.Director;
import ru.sentyurin.repository.DirectorRepository;
import ru.sentyurin.repository.MovieRepository;
import ru.sentyurin.service.DirectorService;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@Service
public class DirectorServiceImpl implements DirectorService {

	private final DirectorRepository directorRepository;
	private final MovieRepository movieRepository;
	private final DirectorDtoMapper dtoMapper;

	@Autowired
	public DirectorServiceImpl(DirectorRepository directorRepository,
			MovieRepository movieRepository, DirectorDtoMapper directorDtoMapper) {
		this.directorRepository = directorRepository;
		this.movieRepository = movieRepository;
		dtoMapper = directorDtoMapper;
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
		director.setId(null);
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
	public Optional<DirectorOutgoingDto> getDirectorById(Integer id) {
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
		Director directorToUpdate = directorRepository.findById(director.getId()).orElseThrow(
				() -> new NoDataInRepositoryException("There is no director with such ID"));
		directorToUpdate.setName(director.getName());
		return mapToOutgoingDto(directorRepository.save(directorToUpdate));
	}

	/**
	 * Deletes director entity from repository
	 */
	@Override
	@Transactional
	public void deleteDirector(Integer id) {
		movieRepository.deleteByDirectorId(id);
		directorRepository.deleteById(id);
	}

	private void directorDataValidation(DirectorIncomingDto director)
			throws IncompleateInputExeption {
		if (director.getName() == null)
			throw new IncompleateInputExeption("There must be a director name");
	}

	private DirectorOutgoingDto mapToOutgoingDto(Director director) {
		return dtoMapper.map(director);
	}

}
