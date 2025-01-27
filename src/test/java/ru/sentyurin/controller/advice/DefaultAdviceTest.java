package ru.sentyurin.controller.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.ServletException;
import ru.sentyurin.util.exception.DataBaseException;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.InconsistentInputException;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

class DefaultAdviceTest {

	private static DefaultAdvice advice = new DefaultAdvice();
	private String expectedMessage;
	private HttpStatus expectedStatus;

	@Test
	void shouldHandleDatabaseException() {
		expectedMessage = "some message";
		expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		DataBaseException exception = new DataBaseException(expectedMessage);
		ResponseEntity<String> response = advice.handleDatabaseException(exception);
		assertEquals(expectedMessage, response.getBody());
		assertEquals(expectedStatus, response.getStatusCode());

	}

	@Test
	void shouldHandleNotFoundException() {
		expectedMessage = "some message";
		expectedStatus = HttpStatus.NOT_FOUND;
		NoDataInRepositoryException exception = new NoDataInRepositoryException(expectedMessage);
		ResponseEntity<String> response = advice.handleNotFoundException(exception);
		assertEquals(expectedMessage, response.getBody());
		assertEquals(expectedStatus, response.getStatusCode());
	}

	@Test
	void shouldHandleJsonProcessingException() {
		expectedStatus = HttpStatus.BAD_REQUEST;
		JsonProcessingException exception = Mockito.mock(JsonProcessingException.class);
		ResponseEntity<String> response = advice.handleJsonProcessingException(exception);
		assertEquals(expectedStatus, response.getStatusCode());
	}

	@Test
	void shouldHandleIncompleateInputExeption() {
		expectedStatus = HttpStatus.BAD_REQUEST;
		IncompleateInputExeption exception = new IncompleateInputExeption(expectedMessage);
		ResponseEntity<String> response = advice.handleIncompleateInputExeption(exception);
		assertEquals(expectedStatus, response.getStatusCode());
	}

	@Test
	void shouldHandleMethodArgumentTypeMismatchException() {
		expectedStatus = HttpStatus.BAD_REQUEST;
		MethodArgumentTypeMismatchException exception = Mockito
				.mock(MethodArgumentTypeMismatchException.class);
		ResponseEntity<String> response = advice
				.handleMethodArgumentTypeMismatchException(exception);
		assertEquals(expectedStatus, response.getStatusCode());
	}

	@Test
	void shouldHandleInconsistentInputException() {
		expectedMessage = "some message";
		expectedStatus = HttpStatus.BAD_REQUEST;
		InconsistentInputException exception = new InconsistentInputException(expectedMessage);
		ResponseEntity<String> response = advice.handleInconsistentInputException(exception);
		assertEquals(expectedMessage, response.getBody());
		assertEquals(expectedStatus, response.getStatusCode());
	}

	@Test
	void shouldHandleIncorrectInputException() {
		expectedMessage = "some message";
		expectedStatus = HttpStatus.BAD_REQUEST;
		IncorrectInputException exception = new IncorrectInputException(expectedMessage);
		ResponseEntity<String> response = advice.handleIncorrectInputException(exception);
		assertEquals(expectedMessage, response.getBody());
		assertEquals(expectedStatus, response.getStatusCode());
	}

	@Test
	void shouldHandleServletException() {
		expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		ServletException exception = new ServletException();
		ResponseEntity<String> response = advice.handleServletException(exception);
		assertEquals(expectedStatus, response.getStatusCode());
	}

	@Test
	void shouldHandlePSQLException() {
		expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		PSQLException exception = Mockito.mock(PSQLException.class);
		ResponseEntity<String> response = advice.handlePSQLException(exception);
		assertEquals(expectedStatus, response.getStatusCode());
	}

}
