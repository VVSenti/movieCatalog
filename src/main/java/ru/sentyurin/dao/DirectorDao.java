package ru.sentyurin.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import ru.sentyurin.model.Director;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@org.springframework.stereotype.Repository
public class DirectorDao implements Dao<Director, Integer> {

	private static final String GET_ALL_HQL = "from Director d left join fetch d.movies";

	private static final String GET_BY_ID_HQL = "from Director d left join fetch d.movies where d.id = :id";

	private static final String GET_BY_NAME_HQL = "from Director d where d.name = :name";

	private static final String DELETE_MOVIES_BY_DIRECTOR_ID_HQL = "delete from Movie where director_id = :id";

	private static final String DELETE_DIRECTOR_BY_ID_HQL = "delete from Director where id = :id";

	private final EntityManager entityManager;

	@Autowired
	public DirectorDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Saves a director entity. If there is a director with the same {@code name} it
	 * returns director entity from DB.
	 * 
	 * @return saved director entity
	 */
	@Override
	public Director save(Director director) {
		Optional<Director> directorInDB = findByName(director.getName());
		if (directorInDB.isPresent()) {
			return directorInDB.get();
		}
		entityManager.persist(director);
		return director;
	}

	/**
	 * Returns all director entities from DB. Fields {@code movies} will be null.
	 */
	@Override
	public List<Director> findAll() {
		return entityManager.createQuery(GET_ALL_HQL, Director.class).getResultList();
	}

	/**
	 * Returns a director entity with specified ID from DB.
	 */
	@Override
	public Optional<Director> findById(Integer id) {
		List<Director> directors = entityManager.createQuery(GET_BY_ID_HQL, Director.class)
				.setParameter("id", id).getResultList();
		if (directors.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(directors.getFirst());
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
		entityManager.createNativeQuery(DELETE_MOVIES_BY_DIRECTOR_ID_HQL).setParameter("id", id)
				.executeUpdate();
		return entityManager.createQuery(DELETE_DIRECTOR_BY_ID_HQL).setParameter("id", id)
				.executeUpdate() > 0;
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
		Director directorToUpdate = entityManager.find(Director.class, director.getId());
		if (directorToUpdate == null) {
			throw new NoDataInRepositoryException("There is no director with this id");
		}
		entityManager.merge(director);
		return Optional.of(director);
	}

	/**
	 * Checks if an entity with specified ID is persisted in DB.
	 * 
	 * @return {@code true} if an entity with this ID exists in DB and {@code false}
	 *         in another case
	 */
	@Override
	public boolean isPresentWithId(Integer id) {
		return entityManager.find(Director.class, id) != null;
	}

	private Optional<Director> findByName(String name) {
		List<Director> directors = entityManager.createQuery(GET_BY_NAME_HQL, Director.class)
				.setParameter("name", name).getResultList();
		if (directors.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(directors.getFirst());
		}
	}
}