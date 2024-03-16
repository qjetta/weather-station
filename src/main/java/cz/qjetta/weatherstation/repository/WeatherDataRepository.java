package cz.qjetta.weatherstation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cz.qjetta.weatherstation.model.WeatherData;

@Repository
public interface WeatherDataRepository
		extends MongoRepository<WeatherData, String> {

	List<WeatherData> findByMetadata_StationIdAndTimestampBetween(
			String stationId, LocalDateTime start, LocalDateTime end);

}