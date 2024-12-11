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
import ru.sentyurin.util.exсeption.InconsistentInputException;
import ru.sentyurin.util.exсeption.IncorrectInputException;
import ru.sentyurin.util.exсeption.NoDataInRepository;

public class MovieRepository implements Repository<Movie, Integer> {

	private static final String GET_ALL_MOVIES_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id " + "order by m.id";

	private static final String GET_ALL_MOVIES_BY_DIRECTOR_ID_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id where d.id=? order by m.id";

	private static final String GET_MOVIE_BY_ID_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id " + "where m.id = ?";

	private static final String GET_MOVIE_BY_TITLE_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id "
			+ "where m.title = ?";

	private static final String SAVE_MOVIE_SQL = "insert into Movie(title, release_year, director_id) values (?,?,?)";

	private static final String DELETE_BY_ID_SQL = "delete from Movie where id = ?";

	private static final String DELETE_BY_DIRECTOR_ID_SQL = "delete from Movie where director_id=?";

	private static final String UPDATE_BY_ID_SQL = "update Movie set title=?, release_year=?, director_id=? where id=?";

	private static final String CHECK_BY_ID_SQL = "select id from Movie where id=?";

	private final MovieResultSetMapper resultSetMapper;
	private ConnectionManager connectionManager;
	private Repository<Director, Integer> directorRepository;

	public MovieRepository() {
		resultSetMapper = new MovieResultSetMapper();
		connectionManager = new ConnectionToDbManager();
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public Repository<Director, Integer> getDirectorRepository() {
		return directorRepository;
	}

	public void setDirectorRepository(Repository<Director, Integer> directorRepository) {
		this.directorRepository = directorRepository;
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
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean deleteById(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL);) {
			statement.setInt(1, id);
			return statement.executeUpdate() == 1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean deleteByDirectorId(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(DELETE_BY_DIRECTOR_ID_SQL);) {
			statement.setInt(1, id);
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<Movie> update(Movie movie) throws NoDataInRepository {
		if (!isPresentWithId(movie.getId())) {
			throw new NoDataInRepository("There is no movie with this id");
		}
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID_SQL);) {
			statement.setString(1, movie.getTitle());
			statement.setInt(2, movie.getReleaseYear());
			statement.setInt(3, movie.getDirector().getId());
			statement.setInt(4, movie.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return findById(movie.getId());
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
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isPresentWithId(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(CHECK_BY_ID_SQL)) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Movie> findByDirectorId(Integer id) {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(GET_ALL_MOVIES_BY_DIRECTOR_ID_SQL);) {
			statement.setInt(1, id);
			return resultSetMapper.map(statement.executeQuery());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initDb() {
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection
						.prepareStatement("create table if not exists Movie ( "
								+ "id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY, "
								+ "title varchar UNIQUE NOT NULL, " + "release_year int NOT NULL, "
								+ "director_id int NOT NULL)")) {
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;

	}
}
