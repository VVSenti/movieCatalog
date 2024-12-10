package ru.sentyurin.service.impl;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.MovieRepository;
import ru.sentyurin.repository.Repository;
import ru.sentyurin.repository.RepositoryFactory;
import ru.sentyurin.service.DirectorService;
import ru.sentyurin.service.MovieService;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.util.exсeption.IncompleateInputExeption;
import ru.sentyurin.util.exсeption.IncorrectInputException;
import ru.sentyurin.util.exсeption.NoDataInRepository;

public class DirectorServiceImpl implements DirectorService {

	private Repository<Director, Integer> directorRepository;

	@SuppressWarnings("unchecked")
	public DirectorServiceImpl() {
		directorRepository = (Repository<Director, Integer>) RepositoryFactory
				.getRepository(Director.class);
	}

	@Override
	public DirectorOutgoingDto createDirector(DirectorIncomingDto director)
			throws IncompleateInputExeption, IncorrectInputException {
		directorDataValidation(director);
		return new DirectorOutgoingDto(directorRepository.save(director.toDirector()));
	}

	@Override
	public List<DirectorOutgoingDto> getDirectors() {
		return directorRepository.findAll().stream().map(DirectorOutgoingDto::new).toList();
	}

	@Override
	public Optional<DirectorOutgoingDto> getDirectorById(int id) {
		Optional<Director> optionalDirector = directorRepository.findById(id);
		return optionalDirector.isEmpty() ? Optional.empty()
				: Optional.of(new DirectorOutgoingDto(optionalDirector.get()));
	}

	@Override
	public DirectorOutgoingDto updateDirector(DirectorIncomingDto director) {
		if (director.getId() == null)
			throw new IncompleateInputExeption("There must be a director ID");
		directorDataValidation(director);
		return new DirectorOutgoingDto(directorRepository.update(director.toDirector()).get());
	}

	@Override
	public boolean deleteDirector(int id) {
		return directorRepository.deleteById(id);
	}

	private void directorDataValidation(DirectorIncomingDto director) {
		if (director.getName() == null)
			throw new IncompleateInputExeption("There must be a director name");
	}
}
