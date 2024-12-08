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
import ru.sentyurin.repository.mapper.MovieResultSetMapper;
import ru.sentyurin.util.exeption.InconsistentInputException;
import ru.sentyurin.util.exeption.IncorrectInputException;

public class MovieRepository implements Repository<Movie, Integer> {

	private static final String GET_ALL_MOVIES_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id";

	private static final String GET_MOVIE_BY_ID_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id " + "where m.id = ?";

	private static final String GET_MOVIE_BY_TITLE_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id "
			+ "where m.title = ?";

	private static final String SAVE_MOVIE_SQL = "insert into Movie(title, release_year, director_id) values (?, ?, ?)";
	
	private static final String DELETE_MOVIE_BY_ID_SQL = "delete from Movie where id = ?";

	private final MovieResultSetMapper resultSetMapper;
	private final ConnectionManager connectionManager;
	private final Repository<Director, Integer> directorRepository;

	public MovieRepository() {
		resultSetMapper = new MovieResultSetMapper();
		connectionManager = new ConnectionToDbManager();
		directorRepository = new DirectorRepository();
	}

	@Override
	public Movie save(Movie movie) throws InconsistentInputException {
		Optional<Movie> movieInDB = findByTitle(movie.getTitle());
		if (movieInDB.isPresent()) {
			return movieInDB.get();
		}

		Director director = movie.getDirector();
		Director directorInDB;
		if (director.getName() != null) {
			directorInDB = directorRepository.save(director);
			if (director.getId() != null && !director.getId().equals(directorInDB.getId())) {
				throw new InconsistentInputException("Director with this id has another name");
			}
		} else {
			directorInDB = directorRepository.findById(director.getId()).orElseThrow(
					() -> new IncorrectInputException("There is no director with this ID"));
		}

		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(SAVE_MOVIE_SQL);) {
			statement.setString(1, movie.getTitle());
			statement.setInt(2, movie.getReleaseYear());
			statement.setInt(3, directorInDB.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return findByTitle(movie.getTitle()).orElseThrow();
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
	public Optional<Movie> findById(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_MOVIE_BY_ID_SQL);) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			List<Movie> movies = resultSetMapper.map(resultSet);
			if (movies.isEmpty())
				return Optional.empty();
			return Optional.of(movies.getFirst());
		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	@Override
	public boolean deleteById(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(DELETE_MOVIE_BY_ID_SQL);) {
			statement.setInt(1, id);
			return statement.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Optional<Movie> findByTitle(String title) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(GET_MOVIE_BY_TITLE_SQL);) {
			statement.setString(1, title);
			ResultSet resultSet = statement.executeQuery();
			List<Movie> movies = resultSetMapper.map(resultSet);
			if (movies.isEmpty())
				return Optional.empty();
			return Optional.of(movies.getFirst());
		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
