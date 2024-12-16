package ru.sentyurin.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ru.sentyurin.db.ConnectionManager;
import ru.sentyurin.db.ConnectionToDbManager;
import ru.sentyurin.model.Director;
import ru.sentyurin.repository.mapper.DirectorResultSetMapper;
import ru.sentyurin.util.exception.DataBaseException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

public class DirectorRepository implements Repository<Director, Integer> {

	private static final String GET_ALL_SQL = "select id, name from Director";

	private static final String GET_BY_ID_SQL = "select id, name from Director where id = ?";

	private static final String GET_BY_NAME_SQL = "select id, name from Director where name = ?";

	private static final String SAVE_SQL = "insert into Director(name) values(?)";

	private static final String CHECK_BY_ID_SQL = "select id from Director where id=?";

	private static final String DELETE_BY_ID_SQL = "delete from Director where id = ?";

	private static final String UPDATE_BY_ID_SQL = "update Director set name=? where id=?";

	private final DirectorResultSetMapper resultSetMapper;
	private ConnectionManager connectionManager;
	private MovieRepository movieRepository;

	public DirectorRepository() {
		resultSetMapper = new DirectorResultSetMapper();
		connectionManager = new ConnectionToDbManager();
	}

	/**
	 * Returns {@code ConnectionManager}
	 */
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	/**
	 * Sets {@code ConnectionManager}
	 * 
	 * @param connectionManager
	 */
	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * Returns a repository of movie entities
	 */
	public MovieRepository getMovieRepository() {
		return movieRepository;
	}

	/**
	 * Sets a repository of movie entities
	 * 
	 * @param directorRepository
	 */
	public void setMovieRepository(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	/**
	 * Saves a director entity. If there is a director with the same {@code name} it
	 * returns director entity from DB.
	 * 
	 * @return saved director entity
	 */
	@Override
	public Director save(Director director) {
		String directorName = director.getName();
		Optional<Director> directorInDB = findByName(directorName);
		if (directorInDB.isPresent()) {
			return directorInDB.get();
		}
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SAVE_SQL)) {
			statement.setString(1, directorName);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
		return findByName(directorName).orElse(null);

	}

	/**
	 * Returns all director entities from DB. Fields {@code movies} will be null.
	 */
	@Override
	public List<Director> findAll() {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_ALL_SQL);
				ResultSet resultSet = statement.executeQuery()) {
			return resultSetMapper.map(resultSet);
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	/**
	 * Returns a director entity with specified ID from DB.
	 */
	@Override
	public Optional<Director> findById(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL)) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			List<Director> directors = resultSetMapper.map(resultSet);
			if (directors.isEmpty())
				return Optional.empty();
			Director director = directors.get(0);
			director.setMovies(movieRepository.findByDirectorId(id));
			return Optional.of(director);
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	/**
	 * Deletes a director entity with specified ID from DB
	 * 
	 * @param id
	 * @return {@code true} if an entity has been deleted and {@code false} in
	 *         another case
	 */
	@Override
	public boolean deleteById(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL);) {
			statement.setInt(1, id);
			if (statement.executeUpdate() == 0) {
				return false;
			}
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
		movieRepository.deleteByDirectorId(id);
		return true;
	}

	/**
	 * Updates a movie entity with specified ID in DB.
	 * 
	 * @throws NoDataInRepositoryException if there is no director entity with
	 *                                     specified ID in DB
	 * 
	 * @return updated movie entity
	 */
	@Override
	public Optional<Director> update(Director director) {
		if (!isPresentWithId(director.getId())) {
			throw new NoDataInRepositoryException("There is no movie with this id");
		}
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID_SQL);) {
			statement.setString(1, director.getName());
			statement.setInt(2, director.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
		return findById(director.getId());
	}

	/**
	 * Checks if an entity with specified ID is persisted in DB.
	 * 
	 * @return {@code true} if an entity with this ID exists in DB and {@code false}
	 *         in another case
	 */
	@Override
	public boolean isPresentWithId(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(CHECK_BY_ID_SQL)) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	/**
	 * Creates a table in DB to persist director entities if it doesn't exist yet.
	 */
	@Override
	public void initDb() {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement("create table if not exists Director ( "
								+ "id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY, "
								+ "name varchar UNIQUE NOT NULL)")) {
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	private Optional<Director> findByName(String name) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_NAME_SQL);) {
			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();
			List<Director> directors = resultSetMapper.map(resultSet);
			return directors.isEmpty() ? Optional.empty() : Optional.of(directors.get(0));
		} catch (SQLException e) {
			throw new DataBaseException(e.getMessage());
		}
	}

}
