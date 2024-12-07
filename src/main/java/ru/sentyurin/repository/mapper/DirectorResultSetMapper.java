package ru.sentyurin.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.sentyurin.model.Director;

public class DirectorResultSetMapper implements ResultSetMapper<Director> {

	@Override
	public List<Director> map(ResultSet resultSet) throws SQLException {
		List<Director> directors = new ArrayList<>();
		while (resultSet.next()) {
			Director director = new Director();
			director.setId(resultSet.getInt("id"));
			director.setName(resultSet.getString("name"));
			directors.add(director);
		}
		return directors;
	}

}
