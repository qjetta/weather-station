package cz.qjetta.weatherstation;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class WeatherDtoValidationTest {

	@Test
	public void testValidationSuccess() {
		WeatherDataDto dto = WeatherTestHelper.createRec(1);
		expectsSuccesfullValidation(dto);
	}

	@Test
	public void testInvalidHumidity() {
		WeatherDataDto dto = WeatherTestHelper.createRec(1);
		dto.setHumidity(200);

		expectsErrorValidation(dto);
	}

	@Test
	public void testInvalidPrecipitation() {
		WeatherDataDto dto = WeatherTestHelper.createRec(1);
		dto.setPrecipitation(-1);

		expectsErrorValidation(dto);
	}

	@Test
	public void testInvalidWind() {
		WeatherDataDto dto = WeatherTestHelper.createRec(1);
		dto.setWind(-5);

		expectsErrorValidation(dto);
	}

	@Test
	public void testInvalidEmptyTimestamp() {
		WeatherDataDto dto = WeatherTestHelper.createRec(1);
		dto.setTimestamp(null);

		expectsErrorValidation(dto);
	}

	@Test
	public void testInvalidFutureTimestamp() {
		WeatherDataDto dto = WeatherTestHelper.createRec(1);
		dto.setTimestamp(LocalDateTime.now().plusHours(1));

		expectsErrorValidation(dto);
	}

	private void expectsErrorValidation(WeatherDataDto dto) {
		Set<ConstraintViolation<WeatherDataDto>> violations = validate(dto);
		assertFalse(violations.isEmpty());
	}

	private void expectsSuccesfullValidation(WeatherDataDto dto) {
		Set<ConstraintViolation<WeatherDataDto>> violations = validate(dto);
		assertTrue(violations.isEmpty());
	}

	private Set<ConstraintViolation<WeatherDataDto>> validate(
			WeatherDataDto dto) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<WeatherDataDto>> violations = validator
				.validate(dto);
		return violations;
	}
}