package ru.sentyurin.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ru.sentyurin.model.Director;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@org.springframework.stereotype.Repository
public class DirectorRepositoryHiber implements Repository<Director, Integer> {

	private static final String GET_ALL_HQL = "from Director d left join fetch d.movies";

	private static final String GET_BY_ID_HQL = "from Director d left join fetch d.movies where d.id = :id";

	private static final String GET_BY_NAME_HQL = "from Director d where d.name = :name";

	private static final String DELETE_MOVIES_BY_DIRECTOR_ID_SQL = "delete from Movie where director_id=?";

	private final SessionFactory sessionFactory;

	@Autowired
	public DirectorRepositoryHiber(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;	
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
		Session session = sessionFactory.getCurrentSession();
		session.persist(director);
		return director;
	}

	/**
	 * Returns all director entities from DB. Fields {@code movies} will be null.
	 */
	@Override
	public List<Director> findAll() {
		return sessionFactory.getCurrentSession().createQuery(GET_ALL_HQL, Director.class).list();
	}

	/**
	 * Returns a director entity with specified ID from DB.
	 */
	@Override
	public Optional<Director> findById(Integer id) {
		return sessionFactory.getCurrentSession().createQuery(GET_BY_ID_HQL, Director.class)
				.setParameter("id", id).uniqueResultOptional();
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
		Session session = sessionFactory.getCurrentSession();
		Director director = session.get(Director.class, id);
		if (director != null) {
			session.remove(director);
		}
		return director != null;
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
		Session session = sessionFactory.getCurrentSession();
		Director directorToUpdate = session.get(Director.class, director.getId());
		if (directorToUpdate == null) {
			session.getTransaction().rollback();
			throw new NoDataInRepositoryException("There is no director with this id");
		}
		session.merge(director);
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
		return sessionFactory.getCurrentSession().get(Director.class, id) != null;
	}

	private Optional<Director> findByName(String name) {
		Session session = sessionFactory.getCurrentSession();
		return session.createQuery(GET_BY_NAME_HQL, Director.class).setParameter("name", name)
				.uniqueResultOptional();
	}
}