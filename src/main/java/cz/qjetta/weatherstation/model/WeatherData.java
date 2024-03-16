package cz.qjetta.weatherstation.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.TimeSeries;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TimeSeries(timeField = "timestamp", metaField = "metadata", collection = "data")
public class WeatherData {
	@Id
	@Field("_id")
	private String id;
	private LocalDateTime timestamp;
	private double temp;
	private int humidity;
	private int wind;
	private double precipitation;
	private Metadata metadata;

}