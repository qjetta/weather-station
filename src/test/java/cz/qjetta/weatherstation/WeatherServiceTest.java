package cz.qjetta.weatherstation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import cz.qjetta.weatherstation.dto.WeatherDtoMapper;
import cz.qjetta.weatherstation.dto.WeatherDtoMapperImpl;
import cz.qjetta.weatherstation.model.WeatherData;
import cz.qjetta.weatherstation.repository.WeatherDataRepository;
import cz.qjetta.weatherstation.service.WeatherDataService;

@ExtendWith(SpringExtension.class)
public class WeatherServiceTest {

	@Mock
	WeatherDataRepository repository;

	@Spy
	WeatherDtoMapper weatherDtoMapper = new WeatherDtoMapperImpl();

	@InjectMocks
	WeatherDataService service;

	@Test
	void testFindAllWithoutParameters() {
		WeatherData rec1 = weatherDtoMapper
				.convertFromInsertDto(
						WeatherTestHelper.createRec(1));
		WeatherData rec2 = weatherDtoMapper
				.convertFromInsertDto(
						WeatherTestHelper.createRec(2));
		List<WeatherData> indList = Arrays.asList(rec1,
				rec2);

		when(repository.findByMetadata_StationId(any(),
				any())).thenReturn(
						new PageImpl<WeatherData>(indList));

		// test
		List<WeatherDataDto> returnedList = service
				.getAllWeatherData(0, 10, "s1");

		assertThat(returnedList).isNotNull();
		assertThat(returnedList).hasSize(2);
		verify(repository, times(1))
				.findByMetadata_StationId(any(), any());
	}

	@Test
	void testFindByWithResult() {
		WeatherDataDto rec1 = WeatherTestHelper
				.createRec(1);
		WeatherDataDto rec2 = WeatherTestHelper
				.createRec(2);
		List<WeatherDataDto> indList = Arrays.asList(rec1,
				rec2);

		LocalDateTime begin = LocalDateTime.now()
				.minusDays(20);
		LocalDateTime end = LocalDateTime.now()
				.minusDays(1);

		Pageable pageable = WeatherTestHelper
				.createDefaultPageable();

		when(repository
				.findByMetadata_StationIdAndTimestampBetweenOrderByTimestamp(
						"s1", begin, end, pageable))
				.thenReturn(indList);

		// test
		List<WeatherDataDto> returnedList = service
				.findByMetadataStationIdAndTimestampBetween(
						"s1", begin, end, 0, 20);

		assertThat(returnedList).isNotNull();
		assertThat(returnedList).hasSize(2);
		verify(repository, times(1))
				.findByMetadata_StationIdAndTimestampBetweenOrderByTimestamp(
						"s1", begin, end, pageable);
	}

	@Test
	void testFindByWithResuEmptyList() {
		List<WeatherDataDto> indList = Arrays.asList();

		LocalDateTime begin = LocalDateTime.now()
				.minusDays(20);
		LocalDateTime end = LocalDateTime.now()
				.minusDays(1);

		Pageable pageable = WeatherTestHelper
				.createDefaultPageable();

		when(repository
				.findByMetadata_StationIdAndTimestampBetweenOrderByTimestamp(
						"s1", begin, end, pageable))
				.thenReturn(indList);

		// test
		List<WeatherDataDto> returnedList = service
				.findByMetadataStationIdAndTimestampBetween(
						"s1", begin, end, 0, 20);

		assertThat(returnedList).isNotNull();
		assertThat(returnedList).hasSize(0);
		verify(repository, times(1))
				.findByMetadata_StationIdAndTimestampBetweenOrderByTimestamp(
						"s1", begin, end, pageable);
	}

	@Test
	void testInsertValid() {
		WeatherDataDto dto = WeatherTestHelper.createRec(1);
		WeatherData weatherData = weatherDtoMapper
				.convertFromInsertDto(dto);

		when(repository.save(weatherData))
				.thenReturn(weatherData);

		// test
		service.insertWeatherData(dto);

		verify(repository, times(1)).save(weatherData);
	}

}