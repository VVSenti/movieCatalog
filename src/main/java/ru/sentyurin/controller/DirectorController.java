package ru.sentyurin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonProcessingException;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.service.DirectorService;
import ru.sentyurin.util.exception.DataBaseException;
import ru.sentyurin.util.exception.IncompleateInputExeption;
import ru.sentyurin.util.exception.NoDataInRepositoryException;

@RestController
@RequestMapping("/directors")
public class DirectorController {
	private static final String NO_DIRECTOR_WITH_ID_MSG = "There is no director with this ID";

	private final DirectorService directorService;

	@Autowired
	public DirectorController(DirectorService directorServiceImpl) {
		directorService = directorServiceImpl;
	}

	@GetMapping
	public List<DirectorOutgoingDto> doGet() {
		return directorService.getDirectors();
	}

	@GetMapping("/{id}")
	public DirectorOutgoingDto doGetById(@PathVariable Integer id) {
		return directorService.getDirectorById(id)
				.orElseThrow(() -> new NoDataInRepositoryException(NO_DIRECTOR_WITH_ID_MSG));
	}

	@PostMapping
	public ResponseEntity<DirectorOutgoingDto> doPost(@RequestBody DirectorIncomingDto input) {
		return new ResponseEntity<>(directorService.createDirector(input), HttpStatus.CREATED);
	}

	@PutMapping
	public DirectorOutgoingDto doPut(@RequestBody DirectorIncomingDto input) {
		return directorService.updateDirector(input);
	}

	@DeleteMapping("/{id}")
	public String doDelete(@PathVariable Integer id) {
		if (directorService.deleteDirector(id)) {
			return String.format("Director with id %d has been deleted", id);
		} else {
			throw new NoDataInRepositoryException(NO_DIRECTOR_WITH_ID_MSG);
		}
	}

	@ExceptionHandler
	private ResponseEntity<String> handleDatabaseException(DataBaseException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleNotFoundException(NoDataInRepositoryException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleJsonProcessingException(
			JsonProcessingException exception) {
		return new ResponseEntity<>("Bad input JSON", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleIncompleateInputExeption(
			IncompleateInputExeption exeption) {
		return new ResponseEntity<>("Incompleate data: " + exeption.getMessage(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	private ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception){
		return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
	}
}
