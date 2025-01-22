package ru.sentyurin.controller.mapper;

import org.springframework.stereotype.Component;

import ru.sentyurin.controller.dto.MovieIncomingDto;
import ru.sentyurin.controller.dto.MovieOutgoingDto;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

@Component
public class MovieDtoMapperImpl implements MovieDtoMapper {

	@Override
	public Movie map(MovieIncomingDto incomingDto) {
		Director director = new Director();
		director.setId(incomingDto.getDirectorId());
		director.setName(incomingDto.getDirectorName());
		Movie movie = new Movie();
		movie.setId(incomingDto.getId());
		movie.setTitle(incomingDto.getTitle());
		movie.setReleaseYear(incomingDto.getReleaseYear());
		movie.setDirector(director);
		return movie;
	}

	@Override
	public MovieOutgoingDto map(Movie movie) {
		MovieOutgoingDto outgoingDto = new MovieOutgoingDto();
		outgoingDto.setId(movie.getId());
		outgoingDto.setTitle(movie.getTitle());
		outgoingDto.setReleaseYear(movie.getReleaseYear());
		Director director = movie.getDirector();
		if (director != null) {
			outgoingDto.setDirectorId(director.getId());
			outgoingDto.setDirectorName(director.getName());
		}
		return outgoingDto;
	}
}
