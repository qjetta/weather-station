package cz.qjetta.weatherstation.prediction;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import cz.qjetta.weatherstation.exception.NoDataForPredictionFound;
import cz.qjetta.weatherstation.model.TimestampAndTemperature;
import cz.qjetta.weatherstation.model.TimestampAndTemperatureWithId;

/**
 * It predicts next values based on {@link SimpleRegression}
 */
public class TemperaturePrediction {

	public List<TimestampAndTemperature> predictFutureTemperatures(
			List<TimestampAndTemperatureWithId> data, int predictionsCount,
			int intervalInSeconds, boolean showLastMeasurements) {

		if (data.isEmpty()) {
			throw new NoDataForPredictionFound();
		}

		int resultListSize = predictionsCount
				+ (showLastMeasurements ? data.size() : 0);
		List<TimestampAndTemperature> resultList = new ArrayList<>(
				resultListSize);

		SimpleRegression regression = new SimpleRegression();
		List<TimestampAndTemperatureWithId> sortedListAsc = sortList(data);
		addDataToRegression(showLastMeasurements, resultList, regression,
				sortedListAsc);

		predictTemperatures(predictionsCount, intervalInSeconds, resultList,
				regression, sortedListAsc);
		return resultList;
	}

	private List<TimestampAndTemperatureWithId> sortList(
			List<TimestampAndTemperatureWithId> data) {
		return data.stream().sorted(
				(o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()))
				.collect(Collectors.toList());
	}

	private void predictTemperatures(int predictionsCount,
			int intervalInSeconds,
			List<TimestampAndTemperature> predictedTemperatures,
			SimpleRegression regression,
			List<TimestampAndTemperatureWithId> sortedListAsc) {
		LocalDateTime nextTimestamp = sortedListAsc
				.get(sortedListAsc.size() - 1).getTimestamp();
		for (int i = 1; i <= predictionsCount; i++) {
			nextTimestamp = nextTimestamp.plusSeconds(intervalInSeconds);
			double predictedTemperature = regression
					.predict(dateToEpochMilli(nextTimestamp));
			TimestampAndTemperature prediction = TimestampAndTemperature
					.builder().timestamp(nextTimestamp)
					.temp(predictedTemperature).predicted(true).build();
			predictedTemperatures.add(prediction);
		}
	}

	private void addDataToRegression(boolean showLastMeasurements,
			List<TimestampAndTemperature> predictedTemperatures,
			SimpleRegression regression,
			List<TimestampAndTemperatureWithId> sortedListAsc) {
		for (TimestampAndTemperatureWithId entry : sortedListAsc) {
			if (showLastMeasurements) {
				predictedTemperatures.add(TimestampAndTemperature.builder()
						.temp(entry.getTemp()).timestamp(entry.getTimestamp())
						.predicted(false).build());
			}
			regression.addData(dateToEpochMilli(entry.getTimestamp()),
					entry.getTemp());
		}
	}

	private long dateToEpochMilli(LocalDateTime date) {
		return date.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
	}
}