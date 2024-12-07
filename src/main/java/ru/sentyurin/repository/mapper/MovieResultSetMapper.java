package ru.sentyurin.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

public class MovieResultSetMapper implements ResultSetMapper<Movie> {

	@Override
	public List<Movie> map(ResultSet resultSet) throws SQLException {
		List<Movie> movies = new ArrayList<>();
		while (resultSet.next()) {
			Movie movie = new Movie();
			movie.setId(resultSet.getInt("id"));
			movie.setTitle(resultSet.getString("title"));
			movie.setReleaseYear(resultSet.getInt("release_year"));
			Director director = new Director();
			director.setId(resultSet.getInt("director_id"));
			director.setName(resultSet.getString("director_name"));
			movie.setDirector(director);
			movies.add(movie);
		}
		return movies;
	}

}
