package ru.sentyurin.service;

import java.util.List;
import java.util.Optional;

import ru.sentyurin.servlet.dto.IncomingDto;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;
import ru.sentyurin.servlet.dto.OutgoingDto;

public interface Service<T> {

	OutgoingDto<T> create(IncomingDto<T> t);

	List<OutgoingDto<T>> getAll();

	Optional<OutgoingDto<T>> getById(int id);
	
	OutgoingDto<T> update(IncomingDto<T> t);

	boolean delete(int id);

}
