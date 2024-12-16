package ru.sentyurin.servlet.dto.java.ru.sentyurin.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sentyurin.config.ConfigLoader;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigLoaderTest {
	
	private Map<String, String> expectedPropertyValues;
	
	@BeforeEach
	void init() {
		expectedPropertyValues = new HashMap<>();
		expectedPropertyValues.put("testDriver", "driverClassName");
		expectedPropertyValues.put("url", "testUrl");
		expectedPropertyValues.put("userName", "testUserName");
		expectedPropertyValues.put("password", "testPassword");
	}
	
	@Test
	void shouldCorrectlyLoadConfigProperties() {
		ConfigLoader configLoader = new ConfigLoader("database.properties.origin");
		assertEquals("testDriver", configLoader.getProperty("driverClassName"));
	}
	
	@Test
	void shouldThrowExceptionIfFileIsAbsent() {
		assertThrows(RuntimeException.class, () -> new ConfigLoader("Absent.File"));
	}

}
