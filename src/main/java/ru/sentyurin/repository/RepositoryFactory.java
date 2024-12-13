package ru.sentyurin.repository;

import java.util.HashMap;
import java.util.Map;

import ru.sentyurin.db.ConnectionManager;
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
	
	/**
	 * Returns a repository with requested entity and ID types
	 * 
	 * @param <T> 
	 * @param <K> 
	 * @param valueClass Class of enity in repository
	 * @param keyClass Class of ID in repository
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, K> Repository<T, K> getRepository(Class<T> valueClass, Class<K> keyClass) {
		return (Repository<T, K>) map.get(valueClass);
	}

	public static void setConnectionManager(ConnectionManager connectionManager) {
		map.values().forEach(v -> v.setConnectionManager(connectionManager));
		map.values().forEach(Repository::initDb);
	}

}
