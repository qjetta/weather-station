package cz.qjetta.weatherstation.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "TemperaturePrediction", description = "Prediction for the temperature")
public class TimestampAndTemperature {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;
	@Schema(name = "temperature")
	private double temp;
	@Schema(description = "If true, it is predicted value. If false, it is the value that is used for prediction(training).")
	private boolean predicted;

}