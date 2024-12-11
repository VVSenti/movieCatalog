package ru.sentyurin.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigLoaderTest {
	
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
