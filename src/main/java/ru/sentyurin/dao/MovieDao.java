package ru.sentyurin.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.util.exception.InconsistentInputException;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@org.springframework.stereotype.Repository
public class MovieDao implements Dao<Movie, Integer> {

	private static final String GET_ALL_MOVIES_HQL = "from Movie m left join fetch m.director order by m.id";

	private static final String GET_ALL_MOVIES_BY_DIRECTOR_ID_HQL = """
			from Movie m left join fetch m.director
			where m.director.id = :id order by m.id""";

	private static final String GET_MOVIE_BY_ID_HQL = "from Movie m left join fetch m.director where m.id = :id";

	private static final String GET_MOVIE_BY_TITLE_HQL = """
			from Movie m left join fetch m.director
			where m.title = :title""";

	private static final String DELETE_BY_DIRECTOR_ID_SQL = "delete from Movie where director_id = :id";

	private static final String DELETE_BY_ID_HQL = "delete from Movie where id = :id";

	private final EntityManager entityManager;
	private final Dao<Director, Integer> directorRepository;

	@Autowired
	public MovieDao(EntityManager entityManager, Dao<Director, Integer> directorRepository) {
		this.entityManager = entityManager;
		this.directorRepository = directorRepository;
	}

	/**
	 * Returns a repository of director entities
	 */
	public Dao<Director, Integer> getDirectorRepository() {
		return directorRepository;
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
		List<Movie> movieInDB = findByTitle(movie.getTitle());
		if (!movieInDB.isEmpty()) {
			return movieInDB.getFirst();
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

		movie.setDirector(directorInDB);
		entityManager.persist(movie);
		return movie;
	}

	/**
	 * Returns all movie entities from DB.
	 */
	@Override
	@Transactional
	public List<Movie> findAll() {
		return entityManager.createQuery(GET_ALL_MOVIES_HQL, Movie.class).getResultList();
	}

	/**
	 * Returns a movie entity with specified ID from DB.
	 */
	@Override
	public Optional<Movie> findById(Integer id) {
		List<Movie> movies = entityManager.createQuery(GET_MOVIE_BY_ID_HQL, Movie.class)
				.setParameter("id", id).getResultList();
		if (movies.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(movies.getFirst());
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
		return entityManager.createQuery(DELETE_BY_ID_HQL).setParameter("id", id)
				.executeUpdate() > 0;
	}

	/**
	 * Deletes a movie with specified director ID from DB
	 * 
	 * @param id
	 * @return {@code true} if an entity has been deleted and {@code false} in
	 *         another case
	 */
	public boolean deleteByDirectorId(Integer id) {
		return entityManager.createNativeQuery(DELETE_BY_DIRECTOR_ID_SQL).setParameter("id", id)
				.executeUpdate() > 0;
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
		if (entityManager.find(Movie.class, movie.getId()) == null) {
			throw new NoDataInRepositoryException("There is no movie with this id");
		}
		Director director = movie.getDirector();
		if (director.getId() != null) {
			Director directorInDb = directorRepository.findById(director.getId()).orElseThrow(
					() -> new NoDataInRepositoryException("There is no director with this ID"));
			// if there is no directorName in input data, it gets it from DB
			// if there is directorName in input data, it is compared with a value from DB
			if (director.getName() == null) {
				director.setName(directorInDb.getName());
			} else {
				if (!directorInDb.getName().equals(director.getName())) {
					throw new InconsistentInputException("Director with this ID has another name: "
							+ directorInDb.getName() + ", but in input  " + director.getName());
				}
			}
		}

		if (director.getName() != null && director.getId() == null) {
			director = directorRepository.save(director);
			movie.setDirector(director);
		}

		entityManager.merge(movie);
		return Optional.of(movie);
	}

	/**
	 * Checks if an entity with specified ID is persisted in DB.
	 * 
	 * @return {@code true} if an entity with this ID exists in DB and {@code false}
	 *         in another case
	 */
	@Override
	public boolean isPresentWithId(Integer id) {
		return entityManager.find(Movie.class, id) != null;
	}

	/**
	 * Returns all movie entities with specified director ID from DB.
	 */
	public List<Movie> findByDirectorId(Integer id) {
		return entityManager.createQuery(GET_ALL_MOVIES_BY_DIRECTOR_ID_HQL, Movie.class)
				.setParameter("id", id).getResultList();
	}

	private List<Movie> findByTitle(String title) {
		return entityManager.createQuery(GET_MOVIE_BY_TITLE_HQL, Movie.class)
				.setParameter("title", title).getResultList();
	}
}
