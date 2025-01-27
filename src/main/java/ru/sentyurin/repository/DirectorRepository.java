package ru.sentyurin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import ru.sentyurin.model.Director;

@org.springframework.stereotype.Repository
public interface DirectorRepository extends Repository<Director, Integer> {

	Director save(Director t);

	List<Director> findAll();

	Optional<Director> findById(Integer id);

	Boolean deleteById(Integer id);

	Boolean existsById(Integer id);

}
