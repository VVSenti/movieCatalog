package ru.sentyurin.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ru.sentyurin.config.ConfigLoader;

public class ConnectionToDbManager implements ConnectionManager {

	@Override
	public Connection getConnection() throws SQLException {
		ConfigLoader configLoader = new ConfigLoader();
		
		try {
			Class.forName(configLoader.getProperty("driverClassName"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return DriverManager.getConnection(configLoader.getProperty("url"),
				configLoader.getProperty("userName"), configLoader.getProperty("password"));
	}

}