package ru.sentyurin.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
	T save(T t);

	List<T> findAll();

	Optional<T> findById(K id);

	boolean deleteById(K id);

	T update(T t);

}
