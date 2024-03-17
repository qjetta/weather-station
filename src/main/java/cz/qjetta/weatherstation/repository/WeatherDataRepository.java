package cz.qjetta.weatherstation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import cz.qjetta.weatherstation.model.WeatherData;

@Repository
public interface WeatherDataRepository
		extends MongoRepository<WeatherData, String> {

	List<WeatherDataDto> findByMetadata_StationIdAndTimestampBetweenOrderByTimestamp(
			String stationId, LocalDateTime start,
			LocalDateTime end, Pageable pageable);

	Page<WeatherData> findByMetadata_StationId(
			String stationId, Pageable pageable);

}