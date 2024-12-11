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
import ru.sentyurin.util.exсeption.IncompleateInputExeption;

public class DirectorServiceImpl implements DirectorService {

	private Repository<Director, Integer> directorRepository;
	private final DirectorDtoMapper dtoMapper;

	@SuppressWarnings("unchecked")
	public DirectorServiceImpl() {
		directorRepository = (Repository<Director, Integer>) RepositoryFactory
				.getRepository(Director.class);
		dtoMapper = new DirectorDtoMapperImpl();
	}

	public Repository<Director, Integer> getDirectorRepository() {
		return directorRepository;
	}

	public void setDirectorRepository(Repository<Director, Integer> directorRepository) {
		this.directorRepository = directorRepository;
	}

	@Override
	public DirectorOutgoingDto createDirector(DirectorIncomingDto director)
			throws IncompleateInputExeption {
		directorDataValidation(director);
		return dtoMapper.map(directorRepository.save(dtoMapper.map(director)));
	}

	@Override
	public List<DirectorOutgoingDto> getDirectors() {
		return directorRepository.findAll().stream().map(this::mapToOutgoingDto).toList();
	}

	@Override
	public Optional<DirectorOutgoingDto> getDirectorById(int id) {
		Optional<Director> optionalDirector = directorRepository.findById(id);
		return optionalDirector.isEmpty() ? Optional.empty()
				: Optional.of(mapToOutgoingDto(optionalDirector.get()));
	}

	@Override
	public DirectorOutgoingDto updateDirector(DirectorIncomingDto director) {
		if (director.getId() == null)
			throw new IncompleateInputExeption("There must be a director ID");
		directorDataValidation(director);
		Director updatedDirector = directorRepository.update(mapFromIncomingDto(director)).get();
		return mapToOutgoingDto(updatedDirector);
	}

	@Override
	public boolean deleteDirector(int id) {
		return directorRepository.deleteById(id);
	}

	private void directorDataValidation(DirectorIncomingDto director) {
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
