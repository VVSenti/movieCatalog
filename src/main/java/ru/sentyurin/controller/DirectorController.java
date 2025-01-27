package ru.sentyurin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.service.DirectorService;
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
	public void doDelete(@PathVariable Integer id) {
		directorService.deleteDirector(id);
	}

}
