package cz.qjetta.weatherstation.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.qjetta.weatherstation.exception.BadRequestException;
import cz.qjetta.weatherstation.export.ExcelWeatherDataExporter;
import cz.qjetta.weatherstation.export.IDataExporter;
import cz.qjetta.weatherstation.export.ResponseEntityWithInputStream;
import cz.qjetta.weatherstation.model.TimestampAndTemperature;
import cz.qjetta.weatherstation.model.WeatherData;
import cz.qjetta.weatherstation.service.PredictionService;
import cz.qjetta.weatherstation.service.WeatherDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/weather")
@Tag(name = "Weather stations API", description = "Collecting and retrieving weather data from stations. Data are saved in MongoDB.")

@RequiredArgsConstructor
public class WeatherDataController {

	private static final String XLS_FILE_NAME_WEATHER_DATA = "weather_data";

	private final WeatherDataService weatherDataService;
	private final PredictionService predictionService;

	@Operation(summary = "Save weather data for a station to the database")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Weather data inserted successfully.") })
	@PostMapping
	public ResponseEntity<String> insert(@RequestBody WeatherData weatherData) {
		weatherDataService.insertWeatherData(weatherData);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Weather data inserted successfully.");
	}

	@Operation(summary = "Generate excel file with data for specified station and time range.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Data succesfully generated.") })
	@GetMapping("excel")
	public ResponseEntity<InputStreamResource> exportWeatherDataToExcel(
			@RequestParam String stationId, @RequestParam LocalDateTime start,
			@RequestParam LocalDateTime end) throws IOException {

		List<WeatherData> data = weatherDataService
				.findByMetadataStationIdAndTimestampBetween(stationId, start,
						end);
		IDataExporter dataExporter = new ExcelWeatherDataExporter(data);
		return new ResponseEntityWithInputStream(dataExporter,
				XLS_FILE_NAME_WEATHER_DATA).create(data);
	}

	@Operation(summary = "Returns data for specified station and time range.", description = "If no parameter is set all data are returned. If stationId, start and end is defined, data are filtered.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Data succesfully returned.") })
	@GetMapping
	public ResponseEntity<List<WeatherData>> findAll(
			@RequestParam(required = false) String stationId,
			@RequestParam(required = false) LocalDateTime start,
			@RequestParam(required = false) LocalDateTime end)
			throws IOException {

		if (stationId == null && start == null && end == null) {
			List<WeatherData> allData = weatherDataService.getAllWeatherData();
			return ResponseEntity.ok().body(allData);
		} else if (stationId != null && start != null && end != null) {
			List<WeatherData> data = weatherDataService
					.findByMetadataStationIdAndTimestampBetween(stationId,
							start, end);

			return ResponseEntity.ok().body(data);
		}
		throw new BadRequestException(
				"No parameter must be set or (stationId and start and end) must be set.");
	}

	@Operation(summary = "Returns predicated data based on last 20 values. SimpleRegression from apache library is used.", description = "If no parameter is set all data are returned. If stationId, start and end is defined, data are filtered.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Data succesfully returned.") })
	@GetMapping("prediction")
	public ResponseEntity<List<TimestampAndTemperature>> getPrediction(
			@RequestParam(required = true) String stationId,
			@RequestParam(required = false, defaultValue = "10") int predictionsCount,
			@RequestParam(required = false, defaultValue = "20") int trainingCount,
			@RequestParam(required = false, defaultValue = "15") int intervalInSeconds,
			@RequestParam(required = false, defaultValue = "false") boolean showLastMeasurements)
			throws IOException {

		List<TimestampAndTemperature> data = predictionService
				.getPredictionData(stationId, predictionsCount, trainingCount,
						intervalInSeconds, showLastMeasurements);

		return ResponseEntity.ok().body(data);

	}
}