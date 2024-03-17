package cz.qjetta.weatherstation;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import cz.qjetta.weatherstation.model.Metadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeatherTestHelper {

	public static WeatherDataDto createRec(int index) {
		return WeatherDataDto.builder()
				.metadata(Metadata.builder().stationId("s1")
						.build())
				.humidity(60 + index).temp(-5 + index)
				.precipitation(0.1 * index).wind(index % 5)
				.timestamp(
						LocalDateTime.of(2000, 1, 1, 0, 0)
								.plusDays(index))
				.build();
	}

	public static Pageable createDefaultPageable() {
		Pageable pageable = PageRequest.of(0, 20,
				Direction.ASC, "timestamp");
		return pageable;
	}
}
