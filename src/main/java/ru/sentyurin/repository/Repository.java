package ru.sentyurin.repository;

import java.util.List;

public interface Repository<T, K> {
	T save(T t);

	List<T> findAll();

	T findById(K id);

	boolean deleteById(K id);

}
