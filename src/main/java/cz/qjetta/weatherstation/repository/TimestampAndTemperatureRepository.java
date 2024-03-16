package cz.qjetta.weatherstation.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cz.qjetta.weatherstation.model.TimestampAndTemperatureWithId;

@Repository
public interface TimestampAndTemperatureRepository
		extends MongoRepository<TimestampAndTemperatureWithId, String> {

	List<TimestampAndTemperatureWithId> findByMetadata_StationIdOrderByTimestampDesc(
			String stationId, Pageable pageable);
}