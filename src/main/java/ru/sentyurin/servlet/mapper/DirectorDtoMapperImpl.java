package ru.sentyurin.servlet.mapper;

import java.util.ArrayList;
import java.util.List;

import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;

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
