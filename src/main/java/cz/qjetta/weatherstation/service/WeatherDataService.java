package cz.qjetta.weatherstation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.qjetta.weatherstation.model.WeatherData;
import cz.qjetta.weatherstation.repository.WeatherDataRepository;

@Service
public class WeatherDataService {

	@Autowired
	private WeatherDataRepository weatherDataRepository;

	public void insertWeatherData(WeatherData weatherData) {
		weatherDataRepository.save(weatherData);
	}

	public List<WeatherData> findByMetadataStationIdAndTimestampBetween(
			String stationId, LocalDateTime start, LocalDateTime end) {

		return weatherDataRepository
				.findByMetadata_StationIdAndTimestampBetween(stationId, start,
						end);
	}

	public List<WeatherData> getAllWeatherData() {
		return weatherDataRepository.findAll();
	}
}