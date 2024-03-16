package cz.qjetta.weatherstation.export;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.MediaType;

/**
 * Defines interface for exporter that can create {@link InputStream} 
 * and return its {@link MediaType} and file extension. 
 * It could be used by {@link ResponseEntityWithInputStream}.
 */
public interface IDataExporter {

	InputStream createInputStream() throws IOException;

	MediaType getMediaType();

	String getExtentsion();

}
