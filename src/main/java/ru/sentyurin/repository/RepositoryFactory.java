package ru.sentyurin.repository;

import java.util.HashMap;
import java.util.Map;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

public class RepositoryFactory {
	private static final Map<Class<?>, Repository<?, ?>> map;
	static {
		map = new HashMap<>();
		MovieRepository movieRepository = new MovieRepository();
		DirectorRepository directorRepository = new DirectorRepository();
		movieRepository.setDirectorRepository(directorRepository);
		directorRepository.setMovieRepository(movieRepository);
		map.put(Movie.class, movieRepository);
		map.put(Director.class, directorRepository);
	}

	private RepositoryFactory() {
	}

	public static Repository<?, ?> getRepository(Class<?> clazz) {
		return map.get(clazz);
	}
}
