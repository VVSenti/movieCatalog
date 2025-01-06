package ru.sentyurin.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;

import ru.sentyurin.db.ConnectionManagerHiber;
import ru.sentyurin.model.Director;
import ru.sentyurin.util.exception.DataBaseException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

public class DirectorRepositoryHiber implements Repository<Director, Integer> {

	private static final String GET_ALL_HQL = "from Director";

	private static final String GET_BY_ID_HQL = "from Director d left join fetch d.movies where d.id = :id";

	private static final String GET_BY_NAME_HQL = "from Director d where d.name = :name";

	private static final String DELETE_MOVIES_BY_DIRECTOR_ID_SQL = "delete from Movie where director_id=?";

	private ConnectionManagerHiber connectionManager;

	public DirectorRepositoryHiber() {
	}

	/**
	 * Returns {@code ConnectionManager}
	 */
	public ConnectionManagerHiber getConnectionManager() {
		return connectionManager;
	}

	/**
	 * Sets {@code ConnectionManager}
	 * 
	 * @param connectionManager
	 */
	public void setConnectionManager(ConnectionManagerHiber connectionManager) {
		this.connectionManager = connectionManager;
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
		try (Session session = connectionManager.openSession()) {
			session.beginTransaction();
			session.persist(director);
			session.getTransaction().commit();
			return director;
		} catch (Exception e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	/**
	 * Returns all director entities from DB. Fields {@code movies} will be null.
	 */
	@Override
	public List<Director> findAll() {
		try (Session session = connectionManager.openSession()) {
			session.beginTransaction();
			List<Director> directors = session.createQuery(GET_ALL_HQL, Director.class).list();
			session.getTransaction().commit();
			return directors;
		} catch (Exception e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	/**
	 * Returns a director entity with specified ID from DB.
	 */
	@Override
	public Optional<Director> findById(Integer id) {
		try (Session session = connectionManager.openSession()) {
			session.beginTransaction();
			Optional<Director> maybeDirector = session.createQuery(GET_BY_ID_HQL, Director.class)
					.setParameter("id", id).uniqueResultOptional();
			session.getTransaction().commit();
			return maybeDirector;
		} catch (Exception e) {
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
		try (Session session = connectionManager.openSession()) {
			session.beginTransaction();
			Director director = session.get(Director.class, id);
			if (director != null) {
				session.remove(director);
			}
			session.getTransaction().commit();
			return director != null;
		} catch (Exception e) {
			throw new DataBaseException(e.getMessage());
		}
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
		try (Session session = connectionManager.openSession()) {
			session.beginTransaction();
			Director directorToUpdate = session.get(Director.class, director.getId());
			if (directorToUpdate == null) {
				session.getTransaction().rollback();
				throw new NoDataInRepositoryException("There is no director with this id");
			}
			session.merge(director);
			session.getTransaction().commit();
			return Optional.of(director);
		} catch (NoDataInRepositoryException e) {
			throw e;
		} catch (Exception e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	/**
	 * Checks if an entity with specified ID is persisted in DB.
	 * 
	 * @return {@code true} if an entity with this ID exists in DB and {@code false}
	 *         in another case
	 */
	@Override
	public boolean isPresentWithId(Integer id) {
		try (Session session = connectionManager.openSession()) {
			session.beginTransaction();
			Director director = session.get(Director.class, id);
			session.getTransaction().commit();
			return director != null;
		} catch (Exception e) {
			throw new DataBaseException(e.getMessage());
		}
	}

	private Optional<Director> findByName(String name) {
		try (Session session = connectionManager.openSession()) {
			session.beginTransaction();
			Optional<Director> maybeDirector = session.createQuery(GET_BY_NAME_HQL, Director.class)
					.setParameter("name", name).uniqueResultOptional();
			session.getTransaction().commit();
			return maybeDirector;
		} catch (Exception e) {
			throw new DataBaseException(e.getMessage());
		}
	}
}
