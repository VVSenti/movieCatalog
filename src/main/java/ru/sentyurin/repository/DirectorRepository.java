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
import ru.sentyurin.util.exсeption.NoDataInRepository;

public class DirectorRepository implements Repository<Director, Integer> {

	private static final String GET_ALL_SQL = "select id, name from Director";

	private static final String GET_BY_ID_SQL = "select id, name from Director where id = ?";

	private static final String GET_BY_NAME_SQL = "select id, name from Director where name = ?";

	private static final String SAVE_SQL = "insert into Director(name) values(?)";

	private static final String CHECK_BY_ID_SQL = "select id from Director where id=?";

	private static final String DELETE_BY_ID_SQL = "delete from Director where id = ?";

	private static final String UPDATE_BY_ID_SQL = "update Director set name=? where id=?";

	private final DirectorResultSetMapper resultSetMapper;
	private final ConnectionManager connectionManager;
	private MovieRepository movieRepository;

	public DirectorRepository() {
		resultSetMapper = new DirectorResultSetMapper();
		connectionManager = new ConnectionToDbManager();
	}

	public MovieRepository getMovieRepository() {
		return movieRepository;
	}

	public void setMovieRepository(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

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
			e.printStackTrace();
			return null;
		}
		return findByName(directorName).orElse(null);

	}

	@Override
	public List<Director> findAll() {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_ALL_SQL);
				ResultSet resultSet = statement.executeQuery()) {
			return resultSetMapper.map(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			return List.of();
		}
	}

	@Override
	public Optional<Director> findById(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL)) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			List<Director> directors = resultSetMapper.map(resultSet);
			if (directors.isEmpty())
				return Optional.empty();
			Director director = directors.getFirst();
			director.setMovies(movieRepository.findByDirectorId(id));
			return Optional.of(director);
		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	@Override
	public boolean deleteById(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL);) {
			statement.setInt(1, id);
			if (statement.executeUpdate() == 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		movieRepository.deleteByDirectorId(id);
		return true;
	}

	@Override
	public Optional<Director> update(Director director) {
		if (!isPresentWithId(director.getId())) {
			throw new NoDataInRepository("There is no movie with this id");
		}
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID_SQL);) {
			statement.setString(1, director.getName());
			statement.setInt(2, director.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}
		return findById(director.getId());
	}

	@Override
	public boolean isPresentWithId(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(CHECK_BY_ID_SQL)) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Optional<Director> findByName(String name) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_NAME_SQL);) {
			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();
			List<Director> directors = resultSetMapper.map(resultSet);
			if (directors.isEmpty())
				return Optional.empty();
			return Optional.of(directors.getFirst());
		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
