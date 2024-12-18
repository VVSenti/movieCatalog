package ru.sentyurin.repository;

import java.util.HashMap;
import java.util.Map;

import ru.sentyurin.db.ConnectionManager;
import ru.sentyurin.db.ConnectionToDbManager;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

public class RepositoryFactory {
	private static final Map<Class<?>, Repository<?, ?>> map;
	static {
		map = new HashMap<>();
		MovieRepository movieRepository = new MovieRepository();
		DirectorRepository directorRepository = new DirectorRepository();
		movieRepository.setDirectorRepository(directorRepository);
		map.put(Movie.class, movieRepository);
		map.put(Director.class, directorRepository);
		try {
			ConnectionManager connectionManager = new ConnectionToDbManager();
			setConnectionManager(connectionManager);
		} catch (Exception e) {
			// If there is no database.properties file in resources
			// new ConnectionToDbManager() throws exception
			// repositories remains without ConnectionManager
		}
	}

	private RepositoryFactory() {
	}

	/**
	 * Returns a repository with requested entity and ID types. All repositories use
	 * the same ConnectionManager.
	 * 
	 * @param <T>
	 * @param <K>
	 * @param valueClass Class of enity in repository
	 * @param keyClass   Class of ID in repository
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, K> Repository<T, K> getRepository(Class<T> valueClass, Class<K> keyClass) {
		if (keyClass != Integer.class)
			throw new IllegalArgumentException();
		return (Repository<T, K>) map.get(valueClass);
	}

	/**
	 * Sets ConnectionManager for all repositories and runs init() method for all
	 * repositories
	 * 
	 * @param connectionManager
	 */
	public static void setConnectionManager(ConnectionManager connectionManager) {
		map.values().forEach(v -> v.setConnectionManager(connectionManager));
		map.values().forEach(Repository::initDb);
	}

}
