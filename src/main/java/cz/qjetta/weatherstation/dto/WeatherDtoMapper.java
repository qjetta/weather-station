package cz.qjetta.weatherstation.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cz.qjetta.weatherstation.model.WeatherData;

@Mapper(componentModel = "spring")
public interface WeatherDtoMapper {

	WeatherDataDto convertToInsertDto(WeatherData individual);

	@Mapping(target = "id", ignore = true)
	WeatherData convertFromInsertDto(WeatherDataDto dto);

}
