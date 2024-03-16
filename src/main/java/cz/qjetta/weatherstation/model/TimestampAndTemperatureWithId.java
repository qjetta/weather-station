package cz.qjetta.weatherstation.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "data")
@Data
public class TimestampAndTemperatureWithId {

	@Id
	private String id;
	private LocalDateTime timestamp;
	private double temp;
	private Metadata metadata;

}