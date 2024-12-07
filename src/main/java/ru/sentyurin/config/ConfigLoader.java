package ru.sentyurin.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
	private static final String DEFAUL_PROPERTIES_PATH = "database.properties";
	private Properties properties;

	public ConfigLoader() {
		this(DEFAUL_PROPERTIES_PATH);
	}

	public ConfigLoader(String path) {
		properties = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
			if (input == null) {
				throw new RuntimeException("File " + path + " wasn't found");
			}
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Exception during properties reading from file " + path);
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}