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
import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.mapper.DirectorResultSetMapper;
import ru.sentyurin.repository.mapper.MovieResultSetMapper;

public class DirectorRepository implements Repository<Director, Integer> {

	private static final String GET_ALL_DIRECTORS_SQL = "select id, name from Director";

	private static final String GET_DIRECTOR_BY_ID_SQL = "select id, name from Director where id = ?";
	private static final String GET_DIRECTOR_BY_NAME_SQL = "select id, name from Director where name = ?";

	private static final String SAVE_DIRECTOR_SQL = "insert into Director(name) values(?)";

	private DirectorResultSetMapper resultSetMapper;
	private ConnectionManager connectionManager;

	public DirectorRepository() {
		resultSetMapper = new DirectorResultSetMapper();
		connectionManager = new ConnectionToDbManager();
	}

	@Override
	public Director save(Director director) {
		String directorName = director.getName();
		Optional<Director> directorInDB = findByName(directorName);
		if (directorInDB.isPresent()) {
			return directorInDB.get();
		}
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(SAVE_DIRECTOR_SQL);) {
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
				PreparedStatement statement = connection.prepareStatement(GET_ALL_DIRECTORS_SQL);
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
				PreparedStatement statement = connection
						.prepareStatement(GET_DIRECTOR_BY_ID_SQL);) {
			statement.setInt(1, id);
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

	private Optional<Director> findByName(String name) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(GET_DIRECTOR_BY_NAME_SQL);) {
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

	@Override
	public boolean deleteById(Integer id) {
		return false;
	}

}
