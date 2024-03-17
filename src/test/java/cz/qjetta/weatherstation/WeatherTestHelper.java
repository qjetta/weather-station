package cz.qjetta.weatherstation;

import java.time.LocalDateTime;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import cz.qjetta.weatherstation.model.Metadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeatherTestHelper {

	public static WeatherDataDto createRec(int index) {
		return WeatherDataDto.builder()
				.metadata(Metadata.builder().stationId("s1").build())
				.humidity(60 + index).temp(0 + index).precipitation(0.1 * index)
				.wind(index % 5)
				.timestamp(LocalDateTime.of(2000, 1, 1, 0, 0).plusDays(index))
				.build();
	}
}
