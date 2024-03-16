package cz.qjetta.weatherstation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandlerAdvice {

	@ExceptionHandler(BadRequestException.class)
	ProblemDetail handleBadRequestException(BadRequestException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				e.getMessage());
	}

	@ExceptionHandler(NoDataForPredictionFound.class)
	ProblemDetail handleNoDataForPredictionFound(NoDataForPredictionFound e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"No data for prediction found.");
	}

	@ExceptionHandler(Exception.class)
	ProblemDetail handleException(Exception e) {
		log.error(e.getClass().toString());
		log.error("Unexpected error", e);

		return ProblemDetail.forStatusAndDetail(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Unexpected error" + e.getMessage());
	}

}