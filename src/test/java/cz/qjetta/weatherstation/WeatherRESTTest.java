package cz.qjetta.weatherstation;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.IntStream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import cz.qjetta.weatherstation.repository.WeatherDataRepository;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WeatherRESTTest {

	@Container
	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer(
			DockerImageName
					.parse("mongo:5.0.26-rc0-focal"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WeatherDataRepository repository;

	@BeforeEach
	public void beforeEach() {
		repository.deleteAll();
	}

	@Test
	void getAllEmpty() throws Exception {
		mockMvc.perform(
				get("/weather").param("stationId", "s1"))
				.andExpect(status().isOk()).andExpect(
						jsonPath("$", Matchers.hasSize(0)));
	}

	@Test
	void testInsertMessageDefault() throws Exception {

		WeatherDataDto dto = WeatherTestHelper.createRec(0);
		String requestBody = objectMapper
				.writeValueAsString(dto);

		mockMvc.perform(post("/weather")
				.contentType(MediaType.APPLICATION_JSON)

				.content(requestBody))
				.andExpectAll(status().isCreated())
				.andExpect(content().string(
						"Measurement inserted successfully."));
	}

	@Test
	void testInsertMessageCs() throws Exception {
		WeatherDataDto dto = WeatherTestHelper.createRec(0);
		String requestBody = objectMapper
				.writeValueAsString(dto);

		mockMvc.perform(post("/weather")
				.header("Accept-Language", "cs")
				.contentType(MediaType.APPLICATION_JSON)

				.content(requestBody))
				.andExpectAll(status().isCreated())
				.andExpect(content()
						.string("Měření úspěšně uloženo."));
	}

	@Test
	void getAllWithWrongParameters() throws Exception {
		mockMvc.perform(get("/weather").param("start",
				"2000-01-01T10:00:00"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void trainingDataEmty() throws Exception {
		mockMvc.perform(get("/weather/prediction")
				.param("stationId", "s1"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void insertNonValidData() throws Exception {
		WeatherDataDto invalidData = WeatherTestHelper
				.createRec(0);
		invalidData.setTemp(200);
		insertInvalidData(invalidData);
	}

	@Test
	void insertAndFilterAndPredict() throws Exception {

		// insert 20 entries
		insert20Entries4StationS1();

		// filter 4
		mockMvc.perform(get("/weather")
				.param("stationId", "s1")
				.param("start", "2000-01-01T10:00:00")
				.param("end", "2000-01-05T10:00:00"))
				.andExpectAll(status().isOk()).andExpect(
						jsonPath("$", Matchers.hasSize(4)));

		// filter 0
		mockMvc.perform(get("/weather")
				.param("stationId", "s2")
				.param("start", "2000-01-01T10:00:00")
				.param("end", "2000-01-05T10:00:00"))
				.andExpectAll(status().isOk()).andExpect(
						jsonPath("$", Matchers.hasSize(0)));

		// filter 0
		mockMvc.perform(get("/weather")
				.param("stationId", "s1")
				.param("start", "2001-01-01T10:00:00")
				.param("end", "2002-01-05T10:00:00"))
				.andExpectAll(status().isOk()).andExpect(
						jsonPath("$", Matchers.hasSize(0)));

		// predict 10
		mockMvc.perform(get("/weather/prediction")
				.param("stationId", "s1"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$",
						Matchers.hasSize(10)));

		// predict 30 (with last measurements)
		mockMvc.perform(get("/weather/prediction")
				.param("stationId", "s1")
				.param("showLastMeasurements", "true"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$",
						Matchers.hasSize(30)));

		// predict 1
		mockMvc.perform(get("/weather/prediction")
				.param("stationId", "s1")
				.param("predictionsCount", "1")
				.param("intervalInSeconds", "30"))
				.andExpectAll(status().isOk()).andExpect(
						jsonPath("$", Matchers.hasSize(1)));

		// predict 1, training 5
		mockMvc.perform(get("/weather/prediction")
				.param("stationId", "s1")
				.param("predictionsCount", "1")
				.param("trainingCount", "5")
				.param("intervalInSeconds", "30")
				.param("showLastMeasurements", "true"))
				.andExpectAll(status().isOk()).andExpect(
						jsonPath("$", Matchers.hasSize(6)));

		// generate excel file: 4
		mockMvc.perform(get("/weather/excel")
				.param("stationId", "s1")
				.param("start", "2000-01-01T10:00:00")
				.param("end", "2000-01-05T10:00:00"))
				.andExpectAll(status().isOk())
				.andExpect(content().contentType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

	}

	private void insert20Entries4StationS1() {
		IntStream.rangeClosed(0, 20).forEach(index -> {
			try {
				insertMeasurementAndCheckFindAll(index);
			} catch (Exception e) {
				fail(e.getMessage());
			}
		});
	}

	private void insertMeasurementAndCheckFindAll(int index)
			throws JsonProcessingException, Exception {

		insertWeatherData(index);
		checkResultForFindAll(index);
	}

	private void checkResultForFindAll(int index)
			throws Exception {
		mockMvc.perform(
				get("/weather").param("pageSize", "50")
						.param("stationId", "s1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$",
						Matchers.hasSize(index + 1)))
				.andExpect(jsonPath(
						"$[" + index + "].humidity",
						Matchers.is(60 + index)))
				.andExpect(jsonPath(
						"$[" + index
								+ "].metadata.stationId",
						Matchers.is("s1")));
	}

	private void insertWeatherData(int index)
			throws JsonProcessingException, Exception {
		WeatherDataDto rec1 = WeatherTestHelper
				.createRec(index);
		insertWeatherData(rec1);
	}

	private void insertWeatherData(WeatherDataDto rec1)
			throws JsonProcessingException, Exception {
		String requestBody = objectMapper
				.writeValueAsString(rec1);
		mockMvc.perform(post("/weather")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpectAll(status().isCreated());
	}

	private void insertInvalidData(WeatherDataDto rec1)
			throws JsonProcessingException, Exception {
		String requestBody = objectMapper
				.writeValueAsString(rec1);
		mockMvc.perform(post("/weather")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpectAll(status().is4xxClientError());
	}
}