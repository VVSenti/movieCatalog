package ru.sentyurin.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ru.sentyurin.db.ConnectionManager;

public class DBConnectionProvider implements ConnectionManager {
	private String url;
	private String username;
	private String password;

	public DBConnectionProvider(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

}