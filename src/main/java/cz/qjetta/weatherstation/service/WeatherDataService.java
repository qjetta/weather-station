package cz.qjetta.weatherstation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import cz.qjetta.weatherstation.dto.WeatherDtoMapper;
import cz.qjetta.weatherstation.exception.BadRequestException;
import cz.qjetta.weatherstation.model.WeatherData;
import cz.qjetta.weatherstation.repository.WeatherDataRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class WeatherDataService {

	private final WeatherDataRepository weatherDataRepository;
	private final WeatherDtoMapper dtoMapper;

	public void insertWeatherData(
			@Valid WeatherDataDto weatherData) {

		// mongoDB does not support unique indexes for timeseries yet
		boolean entryAlreadyExists = weatherDataRepository
				.existsByMetadata_StationIdAndTimestamp(
						weatherData.getMetadata()
								.getStationId(),
						weatherData.getTimestamp());
		if (entryAlreadyExists) {
			throw new BadRequestException(
					"Entry for stationId and timestamp already exist.");
		}

		weatherDataRepository.save(dtoMapper
				.convertFromInsertDto(weatherData));
	}

	public List<WeatherDataDto> findByMetadataStationIdAndTimestampBetween(
			String stationId, LocalDateTime start,
			LocalDateTime end, int page, int pageSize) {
		Pageable pageable = createPageable(page, pageSize);
		return weatherDataRepository
				.findByMetadata_StationIdAndTimestampBetweenOrderByTimestamp(
						stationId, start, end, pageable);
	}

	public List<WeatherDataDto> getAllWeatherData(int page,
			int pageSize, String stationId) {
		Pageable pageable = createPageable(page, pageSize);
		Page<WeatherData> pagedData = weatherDataRepository
				.findByMetadata_StationId(stationId,
						pageable);
		return pagedData.stream()
				.map(wd -> dtoMapper.convertToInsertDto(wd))
				.toList();
	}

	private Pageable createPageable(int page,
			int pageSize) {
		Pageable pageable = PageRequest.of(page, pageSize,
				Direction.ASC, "timestamp");
		return pageable;
	}
}