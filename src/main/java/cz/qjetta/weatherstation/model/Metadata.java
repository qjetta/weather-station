package cz.qjetta.weatherstation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Metadata", description = "used for identification the station")
public class Metadata {
	@Schema(description = "station ID", example = "s1")
	private String stationId;
}
