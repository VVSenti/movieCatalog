package ru.sentyurin.controller.mapper;

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.model.Movie;

public interface MovieDtoMapper {

	Movie map(MovieIncomingDto incomingDto);

	MovieOutgoingDto map(Movie simpleEntity);
}
