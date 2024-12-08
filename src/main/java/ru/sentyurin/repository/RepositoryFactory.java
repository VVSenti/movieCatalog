package ru.sentyurin.repository;

import java.util.HashMap;
import java.util.Map;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

public class RepositoryFactory {
	private static final Map<Class<?>, Repository<?, ?>> map;
	static {
		map = new HashMap<>();
		map.put(Movie.class, new MovieRepository());
		map.put(Director.class, new DirectorRepository());
	}

	private RepositoryFactory() {
	}

	public static Repository<?, ?> getRepository(Class<?> clazz) {
		return map.get(clazz);
	}
}
