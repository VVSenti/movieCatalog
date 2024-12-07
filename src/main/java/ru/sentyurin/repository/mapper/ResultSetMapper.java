package ru.sentyurin.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ResultSetMapper<T> {
	List<T> map(ResultSet resultSet) throws SQLException;
}
