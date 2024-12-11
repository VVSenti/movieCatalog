package ru.sentyurin.repository;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.db.ConnectionManager;

public interface Repository<T, K> {
	T save(T t);

	List<T> findAll();

	Optional<T> findById(K id);

	boolean deleteById(K id);

	Optional<T> update(T t);
	
	boolean isPresentWithId(K id);
	
	public ConnectionManager getConnectionManager();

	public void setConnectionManager(ConnectionManager connectionManager);

	void initDb();

}
