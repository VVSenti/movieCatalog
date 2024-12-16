package ru.sentyurin.service.impl;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.model.Director;
import ru.sentyurin.repository.Repository;
import ru.sentyurin.repository.RepositoryFactory;
import ru.sentyurin.service.DirectorService;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;
import ru.sentyurin.servlet.mapper.DirectorDtoMapper;
import ru.sentyurin.servlet.mapper.DirectorDtoMapperImpl;
import ru.sentyurin.util.exception.IncompleateInputExeption;

public class DirectorServiceImpl implements DirectorService {

	private Repository<Director, Integer> directorRepository;
	private final DirectorDtoMapper dtoMapper;

	public DirectorServiceImpl() {
		directorRepository = RepositoryFactory.getRepository(Director.class, Integer.class);
		dtoMapper = new DirectorDtoMapperImpl();
	}

	/**
	 * Gets a repository of director entities
	 */
	public Repository<Director, Integer> getDirectorRepository() {
		return directorRepository;
	}

	/**
	 * Sets a repository of director entities
	 * 
	 * @param directorRepository
	 */
	public void setDirectorRepository(Repository<Director, Integer> directorRepository) {
		this.directorRepository = directorRepository;
	}

	/**
	 * Creates new director entity in repository
	 * 
	 * @throws IncompleateInputExeption if field {@code name} in {@code director} is
	 *                                  {@code null}
	 */
	@Override
	public DirectorOutgoingDto createDirector(DirectorIncomingDto director)
			throws IncompleateInputExeption {
		directorDataValidation(director);
		return dtoMapper.map(directorRepository.save(dtoMapper.map(director)));
	}

	/**
	 * Returns all director entities in repository
	 */
	@Override
	public List<DirectorOutgoingDto> getDirectors() {
		return directorRepository.findAll().stream().map(this::mapToOutgoingDto).toList();
	}

	/**
	 * Returns director entity with specified ID
	 */
	@Override
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
	public DirectorOutgoingDto updateDirector(DirectorIncomingDto director) {
		if (director.getId() == null)
			throw new IncompleateInputExeption("There must be a director ID");
		directorDataValidation(director);
		Director updatedDirector = directorRepository.update(mapFromIncomingDto(director)).get();
		return mapToOutgoingDto(updatedDirector);
	}

	/**
	 * Deletes director entity from repository
	 */
	@Override
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
