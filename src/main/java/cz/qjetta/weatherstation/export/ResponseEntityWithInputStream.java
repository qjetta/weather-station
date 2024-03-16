package cz.qjetta.weatherstation.export;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import cz.qjetta.weatherstation.model.WeatherData;
import lombok.RequiredArgsConstructor;

/**
 * It can created {@link ResponseEntity} with {@link InputStreamResource} for
 * passed {@link IDataExporter}. The file name would be fileName.
 */
@RequiredArgsConstructor
public class ResponseEntityWithInputStream {
	private final IDataExporter dataExporter;
	private final String fileName;

	/**
	 * 
	 * @param data
	 * @return {@link ResponseEntity} with {@link InputStream} returned by
	 *         {@link IDataExporter}
	 * @throws IOException
	 */
	public ResponseEntity<InputStreamResource> create(List<WeatherData> data)
			throws IOException {

		InputStream inputStream = dataExporter.createInputStream();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=."
				+ fileName + dataExporter.getExtentsion());

		// Return Excel file as ResponseEntity
		return ResponseEntity.ok().headers(headers)
				.contentType(dataExporter.getMediaType())
				.body(new InputStreamResource(inputStream));
	}

}
