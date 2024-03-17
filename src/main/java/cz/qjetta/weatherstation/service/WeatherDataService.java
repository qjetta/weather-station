package cz.qjetta.weatherstation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import cz.qjetta.weatherstation.dto.WeatherDtoMapper;
import cz.qjetta.weatherstation.repository.WeatherDataRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class WeatherDataService {

	private final WeatherDataRepository weatherDataRepository;
	private final WeatherDtoMapper dtoMapper;

	public void insertWeatherData(@Valid WeatherDataDto weatherData) {
		weatherDataRepository.save(dtoMapper.convertFromInsertDto(weatherData));
	}

	public List<WeatherDataDto> findByMetadataStationIdAndTimestampBetween(
			String stationId, LocalDateTime start, LocalDateTime end) {

		return weatherDataRepository
				.findByMetadata_StationIdAndTimestampBetweenOrderByTimestamp(
						stationId, start, end);
	}

	public List<WeatherDataDto> getAllWeatherData() {
		return weatherDataRepository.findAll().stream()
				.map(wd -> dtoMapper.convertToInsertDto(wd)).toList();
	}
}