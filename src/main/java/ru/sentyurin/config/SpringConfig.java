package ru.sentyurin.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.sentyurin.util.exception.FileReadingException;

@Configuration
@EnableWebMvc
@ComponentScan("ru.sentyurin")
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@EnableJpaRepositories("ru.sentyurin.repository")
public class SpringConfig implements WebMvcConfigurer {
	private final Environment env;

	@Autowired
	public SpringConfig(Environment env) {
		this.env = env;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getRequiredProperty("db.driver_class"));
		dataSource.setUrl(env.getRequiredProperty("db.url"));
		dataSource.setUsername(env.getRequiredProperty("db.username"));
		dataSource.setPassword(env.getRequiredProperty("db.password"));
		return dataSource;
	}

//	@Bean
//	public LocalSessionFactoryBean sessionFactory() {
//		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//		sessionFactory.setDataSource(dataSource());
//		sessionFactory.setPackagesToScan("ru.sentyurin.model");
//		sessionFactory.setHibernateProperties(hibernateProperties());
//		return sessionFactory;
//	}

//	@Bean
//	public PlatformTransactionManager hibernateTransactionManager() {
//		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//		transactionManager.setSessionFactory(sessionFactory().getObject());
//		return transactionManager;
//	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan("ru.sentyurin.model");
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emf.setJpaProperties(hibernateProperties());
		return emf;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}

	private Properties hibernateProperties() {
		try (InputStream input = getClass().getClassLoader()
				.getResourceAsStream("hibernate.properties")) {
			Properties properties = new Properties();
			properties.load(input);
			return properties;
		} catch (IOException e) {
			throw new FileReadingException(
					"Problem occured during loading a hibernate.properties file");
		}
	}

}
