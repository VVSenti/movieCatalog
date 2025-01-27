package ru.sentyurin.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

@Component
public class DirectorDtoMapperImpl implements DirectorDtoMapper {
	
	MovieDtoMapper movieDtoMapper = new MovieDtoMapperImpl();

	@Override
	public Director map(DirectorIncomingDto incomingDto) {
		Director director = new Director();
		director.setId(incomingDto.getId());
		director.setName(incomingDto.getName());
		return director;
	}

	@Override
	public DirectorOutgoingDto map(Director director) {
		DirectorOutgoingDto outgoingDto = new DirectorOutgoingDto();
		outgoingDto.setId(director.getId());
		outgoingDto.setName(director.getName());
		if (director.getMovies() == null) {
			return outgoingDto;
		}
		List<MovieOutgoingDto> movies = new ArrayList<>();
		for (Movie movie : director.getMovies()) {
			movies.add(movieDtoMapper.map(movie));
		}
		outgoingDto.setMovies(movies);
		return outgoingDto;
	}

}
