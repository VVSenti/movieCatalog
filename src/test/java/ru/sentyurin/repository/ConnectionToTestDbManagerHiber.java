package ru.sentyurin.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.JdbcDatabaseContainer;

import ru.sentyurin.db.ConnectionManagerHiber;

public class ConnectionToTestDbManagerHiber implements ConnectionManagerHiber{

	private final Configuration configuration;
	private final SessionFactory sessionFactory;

	public ConnectionToTestDbManagerHiber(JdbcDatabaseContainer<?> container) {
		configuration = new Configuration();
		configuration.configure();
		configuration.setProperty("hibernate.connection.url", container.getJdbcUrl());
		configuration.setProperty("hibernate.connection.username", container.getUsername());
		configuration.setProperty("hibernate.connection.password", container.getPassword());
		configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
		sessionFactory = configuration.buildSessionFactory();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

}
