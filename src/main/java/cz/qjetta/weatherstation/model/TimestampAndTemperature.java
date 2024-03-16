package cz.qjetta.weatherstation.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimestampAndTemperature {

	private LocalDateTime timestamp;
	private double temp;
	private boolean predicted;

}