package ru.sentyurin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import ru.sentyurin.model.Movie;

@org.springframework.stereotype.Repository
public interface MovieRepository extends Repository<Movie, Integer> {

	Movie save(Movie t);

	List<Movie> findAll();

	Optional<Movie> findById(Integer id);
	
	Boolean deleteById(Integer id);

	@Modifying
	@Query(value = "delete from Movie m where m.director_id =?1", nativeQuery = true)
	Integer deleteByDirectorId(Integer id);

	Boolean existsById(Integer id);

}
