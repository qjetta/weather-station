package cz.qjetta.weatherstation.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;

import cz.qjetta.weatherstation.dto.WeatherDataDto;
import lombok.RequiredArgsConstructor;

/**
 * It created {@link InputStream} for Excel based on the {@link List} of
 * {@link WeatherDataDto} objects.
 */
@RequiredArgsConstructor
public class ExcelWeatherDataExporter
		implements IDataExporter {

	private final List<WeatherDataDto> data;

	@Override
	public InputStream createInputStream()
			throws IOException {

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook
					.createSheet("Weather Data");

			createHeader(sheet);
			createDataRows(data, sheet);

			return convertWorkbookToInputStream(workbook);
		}
	}

	private ByteArrayInputStream convertWorkbookToInputStream(
			Workbook workbook) throws IOException {
		ByteArrayInputStream inputStream;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			workbook.write(outputStream);

			inputStream = new ByteArrayInputStream(
					outputStream.toByteArray());
		}
		return inputStream;
	}

	private void createDataRows(List<WeatherDataDto> data,
			Sheet sheet) {
		int rowNum = 1;
		for (WeatherDataDto weather : data) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(
					weather.getTimestamp().toString());
			row.createCell(1)
					.setCellValue(weather.getTemp());
			row.createCell(2)
					.setCellValue(weather.getHumidity());
			row.createCell(3)
					.setCellValue(weather.getWind());
			row.createCell(4).setCellValue(
					weather.getPrecipitation());
		}
	}

	private void createHeader(Sheet sheet) {
		Row headerRow = sheet.createRow(0);
		String[] columns = { "Timestamp", "Temperature",
				"Humidity", "Wind", "Precipitation" };
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
		}
	}

	@Override
	public MediaType getMediaType() {
		return MediaType.parseMediaType(
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	@Override
	public String getExtentsion() {
		return "xlsx";
	}
}
