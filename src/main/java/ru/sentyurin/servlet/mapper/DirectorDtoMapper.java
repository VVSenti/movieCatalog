package ru.sentyurin.servlet.mapper;

import ru.sentyurin.model.Director;
import ru.sentyurin.servlet.dto.DirectorIncomingDto;
import ru.sentyurin.servlet.dto.DirectorOutgoingDto;

public interface DirectorDtoMapper {

	Director map(DirectorIncomingDto incomingDto);

	DirectorOutgoingDto map(Director director);
}
