package cz.qjetta.weatherstation.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import cz.qjetta.weatherstation.model.Metadata;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "Measurement")
public class WeatherDataDto {
	@Schema(example = "2024-01-01T00:00:00.000Z", requiredMode = RequiredMode.REQUIRED)
	@NotNull
	@PastOrPresent
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;
	@Min(-60)
	@Max(60)
	@Schema(example = "15")
	private double temp;

	@Min(0)
	@Max(100)
	@Schema(example = "70")
	private int humidity;
	@Min(0)
	@Max(120)
	@Schema(example = "2", description = "m/s")
	private int wind;
	@Min(0)
	@Max(2500)
	@Schema(example = "0.2", description = "mm")
	private double precipitation;

	private Metadata metadata;

}