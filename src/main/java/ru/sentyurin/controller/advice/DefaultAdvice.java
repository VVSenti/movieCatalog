package ru.sentyurin.controller.advice;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.ServletException;
import ru.sentyurin.util.exception.DataBaseException;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.InconsistentInputException;
import ru.sentyurin.util.exception.IncorrectInputException;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@ControllerAdvice
public class DefaultAdvice {

	@ExceptionHandler
	public ResponseEntity<String> handleDatabaseException(DataBaseException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	public ResponseEntity<String> handleNotFoundException(NoDataInRepositoryException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException exception) {
		return new ResponseEntity<>("Bad input JSON", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<String> handleIncompleateInputExeption(
			IncompleateInputExeption exeption) {
		return new ResponseEntity<>("Incompleate data: " + exeption.getMessage(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<String> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException exception) {
		return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<String> handleInconsistentInputException(
			InconsistentInputException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<String> handleIncorrectInputException(IncorrectInputException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<String> handleServletException(ServletException exception) {
		return new ResponseEntity<>("Oops! Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler
	public ResponseEntity<String> handlePSQLException(PSQLException exception) {
		return new ResponseEntity<>("Error in database", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
