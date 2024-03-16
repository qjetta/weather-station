package cz.qjetta.weatherstation;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.hamcrest.Matchers;
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

import cz.qjetta.weatherstation.model.Metadata;
import cz.qjetta.weatherstation.model.WeatherData;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WeatherRESTTest {

	@Container
	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer(
			DockerImageName.parse("mongo:5.0.26-rc0-focal"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void insertAndFilterAndPredict() throws Exception {

		// check findAll
		mockMvc.perform(get("/weather")).andExpectAll(status().isOk(),
				content().json(objectMapper.writeValueAsString(List.of())));

		// insert 20 entries
		insert20Entries4StationS1();

		// filter 4
		mockMvc.perform(get("/weather").param("stationId", "s1")
				.param("start", "2000-01-01T10:00:00")
				.param("end", "2000-01-05T10:00:00"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(4)));

		// filter 0
		mockMvc.perform(get("/weather").param("stationId", "s2")
				.param("start", "2000-01-01T10:00:00")
				.param("end", "2000-01-05T10:00:00"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(0)));

		// filter 0
		mockMvc.perform(get("/weather").param("stationId", "s1")
				.param("start", "2001-01-01T10:00:00")
				.param("end", "2002-01-05T10:00:00"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(0)));

		// predict 10
		mockMvc.perform(get("/weather/prediction").param("stationId", "s1"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(10)));

		// predict 30 (with last measurements)
		mockMvc.perform(get("/weather/prediction").param("stationId", "s1")
				.param("showLastMeasurements", "true"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(30)));

		// predict 1
		mockMvc.perform(get("/weather/prediction").param("stationId", "s1")
				.param("predictionsCount", "1")
				.param("intervalInSeconds", "30")).andExpectAll(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));

		// predict 1, training 5
		mockMvc.perform(get("/weather/prediction").param("stationId", "s1")
				.param("predictionsCount", "1").param("trainingCount", "5")
				.param("intervalInSeconds", "30")
				.param("showLastMeasurements", "true"))
				.andExpectAll(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(6)));
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

	private void checkResultForFindAll(int index) throws Exception {
		mockMvc.perform(get("/weather")).andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(index + 1)))
				.andExpect(jsonPath("$[" + index + "].humidity",
						Matchers.is(60 + index)))
				.andExpect(jsonPath("$[" + index + "].metadata.stationId",
						Matchers.is("s1")));
	}

	private void insertWeatherData(int index)
			throws JsonProcessingException, Exception {
		WeatherData rec1 = createRec(index);
		String requestBody = objectMapper.writeValueAsString(rec1);
		mockMvc.perform(post("/weather").contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)).andExpectAll(status().isCreated());
	}

	private WeatherData createRec(int index) {
		return WeatherData.builder()
				.metadata(Metadata.builder().stationId("s1").build())
				.humidity(60 + index).temp(0 + index)
				.precipitation(0 + 0.1 * index).wind(index % 5)
				.timestamp(LocalDateTime.of(2000, 1, 1, 0, 0).plusDays(index))
				.build();
	}
}