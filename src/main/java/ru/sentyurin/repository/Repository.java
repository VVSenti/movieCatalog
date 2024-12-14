package ru.sentyurin.repository;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.db.ConnectionManager;

public interface Repository<T, K> {
	/**
	 * Persists an entity in a repository
	 * 
	 * @param t an entry to persist
	 * @return
	 */
	T save(T t);

	/**
	 * Returnes all entiries in a repository
	 * 
	 * @return
	 */
	List<T> findAll();

	/**
	 * Returnes an Optional of entirie with specified ID in a repository. If there
	 * is no such entry it returnes an empty optional
	 * 
	 * @param t ID of a requested entry
	 * @return
	 */
	Optional<T> findById(K id);

	/**
	 * 
	 * @param id
	 * @return
	 */
	boolean deleteById(K id);

	/**
	 * 
	 * @param t
	 * @return
	 */
	Optional<T> update(T t);

	/**
	 * 
	 * @param id
	 * @return
	 */
	boolean isPresentWithId(K id);

	/**
	 * 
	 * @return
	 */
	public ConnectionManager getConnectionManager();

	/**
	 * 
	 * @param connectionManager
	 */
	public void setConnectionManager(ConnectionManager connectionManager);

	/**
	 * 
	 */
	void initDb();

}
