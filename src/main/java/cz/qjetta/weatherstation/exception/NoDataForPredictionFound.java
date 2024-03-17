package cz.qjetta.weatherstation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Encapsulated error for prediction that has no training data
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoDataForPredictionFound
		extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoDataForPredictionFound() {
		super();
	}
}