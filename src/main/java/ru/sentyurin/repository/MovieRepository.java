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
import ru.sentyurin.util.exception.InconsistentInputException;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

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

	/**
	 * Returns {@code ConnectionManager}
	 */
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	/**
	 * Returns a repository of director entities
	 */
	public Repository<Director, Integer> getDirectorRepository() {
		return directorRepository;
	}

	/**
	 * Sets {@code ConnectionManager}
	 * 
	 * @param connectionManager
	 */
	@Override
	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * Sets a repository of director entities
	 * 
	 * @param directorRepository
	 */
	public void setDirectorRepository(Repository<Director, Integer> directorRepository) {
		this.directorRepository = directorRepository;
	}

	/**
	 * Saves a movie entity. If there is a movie with the same {@code title} it
	 * returns movie entity from DB.
	 * 
	 * There could be not null name and null id in director in {@code movie}. If
	 * director with specified name exists in DB, it will be used. If not, new
	 * director will be created.
	 * 
	 * If director in {@code movie} has both not null id and name, a director with
	 * the same id and name values must be in DB or it @throws
	 * InconsistentInputException.
	 * 
	 * If director in {@code movie} has not null id and null name, a director with
	 * the same id must be in DB or it @throws NoDataInRepositoryException.
	 * 
	 * @return saved movie entity
	 */
	@Override
	public Movie save(Movie movie) throws InconsistentInputException, IncorrectInputException {
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
					() -> new NoDataInRepositoryException("There is no director with this ID"));
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
		return findByTitle(movie.getTitle()).get();
	}

	/**
	 * Returns all movie entities from DB.
	 */
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

	/**
	 * Returns a movie entity with specified ID from DB.
	 */
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

	/**
	 * Deletes a movie entity with specified ID from DB
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
			return statement.executeUpdate() == 1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deletes a movie with specified director ID from DB
	 * 
	 * @param id
	 * @return {@code true} if an entity has been deleted and {@code false} in
	 *         another case
	 */
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

	/**
	 * Updates a movie entity with specified ID in DB.
	 * 
	 * There could be 3 options about director in {@code movie}.
	 * 
	 * 1) Director name is not null, but director ID is null. If director with this
	 * name already exists in DB, it will be used. In another case, it will be
	 * created and used.
	 * 
	 * 2) Director ID is not null, but director name is null. If director with this
	 * ID already exists in DB, it will be used. In not, @throws
	 * NoDataInRepositoryException
	 * 
	 * 3) Both director ID and name are not null. If director with specified ID and
	 * name exists in DB, it will be used. If not, it @throws
	 * InconsistentInputException.
	 * 
	 * 
	 * @throws NoDataInRepositoryException if there is no movie entity with
	 *                                     specified ID in DB
	 * 
	 * @return updated movie entity
	 */
	@Override
	public Optional<Movie> update(Movie movie)
			throws NoDataInRepositoryException, InconsistentInputException {
		if (!isPresentWithId(movie.getId())) {
			throw new NoDataInRepositoryException("There is no movie with this id");
		}
		Director director = movie.getDirector();
		Director directorInDb = null;
		if (director.getId() != null) {
			directorInDb = directorRepository.findById(director.getId()).orElseThrow(
					() -> new NoDataInRepositoryException("There is no director with this ID"));
		}
		if (directorInDb != null && director.getName() != null
				&& !directorInDb.getName().equals(director.getName())) {
			throw new InconsistentInputException("Director with this ID has another name: "
					+ directorInDb.getName() + ", but in input  " + director.getName());
		}

		if (director.getName() != null && director.getId() == null) {
			director = directorRepository.save(director);
		}
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID_SQL);) {
			statement.setString(1, movie.getTitle());
			statement.setInt(2, movie.getReleaseYear());
			statement.setInt(3, director.getId());
			statement.setInt(4, movie.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return findById(movie.getId());
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
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns all movie entities with specified director ID from DB.
	 */
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
	
	/**
	 * Creates a table in DB to persist movie entities if it doesn't exist yet.
	 */
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
}
