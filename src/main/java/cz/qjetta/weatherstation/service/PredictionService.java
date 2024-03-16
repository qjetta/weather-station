package cz.qjetta.weatherstation.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cz.qjetta.weatherstation.model.TimestampAndTemperature;
import cz.qjetta.weatherstation.model.TimestampAndTemperatureWithId;
import cz.qjetta.weatherstation.prediction.TemperaturePrediction;
import cz.qjetta.weatherstation.repository.TimestampAndTemperatureRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PredictionService {

	private final TimestampAndTemperatureRepository topMongoValues;

	public List<TimestampAndTemperature> getPredictionData(String stationId,
			int predictionsCount, int trainingCount, int intervalInSeconds,
			boolean showLastMeasurements) {
		List<TimestampAndTemperatureWithId> findTop20ByOrderByTimestampDesc = topMongoValues
				.findByMetadata_StationIdOrderByTimestampDesc(stationId,
						PageRequest.of(0, trainingCount));

		List<TimestampAndTemperature> predictedFutureTemperatures = new TemperaturePrediction()
				.predictFutureTemperatures(findTop20ByOrderByTimestampDesc,
						predictionsCount, intervalInSeconds,
						showLastMeasurements);

		return predictedFutureTemperatures;
	}

}