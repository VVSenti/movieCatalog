package ru.sentyurin.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

public class ConnectionToDbManagerHiber implements ConnectionManagerHiber{

	private final Configuration configuration;
	private final SessionFactory sessionFactory;

	public ConnectionToDbManagerHiber() {
		configuration = new Configuration();
		configuration.configure();
		configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
		sessionFactory = configuration.buildSessionFactory();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

}
