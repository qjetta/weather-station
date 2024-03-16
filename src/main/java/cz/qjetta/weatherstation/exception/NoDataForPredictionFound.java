package cz.qjetta.weatherstation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoDataForPredictionFound extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoDataForPredictionFound() {
		super();
	}

	public NoDataForPredictionFound(String message, Throwable cause) {
		super(message, cause);
	}

	public NoDataForPredictionFound(String message) {
		super(message);
	}

	public NoDataForPredictionFound(Throwable cause) {
		super(cause);
	}
}