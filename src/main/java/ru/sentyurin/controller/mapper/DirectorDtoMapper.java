package ru.sentyurin.controller.mapper;

import ru.sentyurin.controller.dto.DirectorIncomingDto;
import ru.sentyurin.controller.dto.DirectorOutgoingDto;
import ru.sentyurin.model.Director;

public interface DirectorDtoMapper {

	Director map(DirectorIncomingDto incomingDto);

	DirectorOutgoingDto map(Director director);
}
