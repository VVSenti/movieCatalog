package ru.sentyurin.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ru.sentyurin.db.ConnectionManager;
import ru.sentyurin.db.ConnectionToDbManager;
import ru.sentyurin.model.Movie;
import ru.sentyurin.repository.mapper.MovieResultSetMapper;

public class MovieRepository implements Repository<Movie, Integer> {

	private static final String GET_ALL_MOVIES_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id";

	private MovieResultSetMapper resultSetMapper;
	private ConnectionManager connectionManager;

	public MovieRepository() {
		resultSetMapper = new MovieResultSetMapper();
		connectionManager = new ConnectionToDbManager();
	}

	@Override
	public Movie save(Movie t) {
		return null;
	}

	@Override
	public List<Movie> findAll() {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_ALL_MOVIES_SQL);
				ResultSet resultSet = statement.executeQuery()) {
			return resultSetMapper.map(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			return List.of();
		}

	}

	@Override
	public Movie findById(Integer id) {
		return null;
	}

	@Override
	public boolean deleteById(Integer id) {
		return false;
	}

}
