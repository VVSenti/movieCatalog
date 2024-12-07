package ru.sentyurin.servlet.mapper;

import ru.sentyurin.model.Movie;
import ru.sentyurin.servlet.dto.MovieIncomingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;

public interface MovieDtoMapper {

	Movie map(MovieIncomingDto incomingDto);

	MovieOutgoingDto map(Movie simpleEntity);
}
