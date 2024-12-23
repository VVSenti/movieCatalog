package ru.sentyurin.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ru.sentyurin.config.ConfigLoader;
import ru.sentyurin.util.exception.DataBaseDriverClassNotFound;

public class ConnectionToDbManager implements ConnectionManager {

	private final ConfigLoader configLoader;

	public ConnectionToDbManager() {
		configLoader = new ConfigLoader();
		try {
			Class.forName(configLoader.getProperty("driverClassName"));
		} catch (ClassNotFoundException e) {
			throw new DataBaseDriverClassNotFound(e.getMessage());
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(configLoader.getProperty("url"),
				configLoader.getProperty("userName"), configLoader.getProperty("password"));
	}

}
