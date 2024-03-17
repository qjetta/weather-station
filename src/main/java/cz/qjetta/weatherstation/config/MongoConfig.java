package cz.qjetta.weatherstation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import lombok.RequiredArgsConstructor;

/**
 * this is needed so that _class is not saved in mongoDB
 * http://mwakram.blogspot.com/2017/01/remove-class-from-mongo-documents.html
 */
@Configuration
@RequiredArgsConstructor
public class MongoConfig {

	private final MongoDatabaseFactory mongoDbFactory;
	private final MongoMappingContext mongoMappingContext;

	@Bean
	public MappingMongoConverter mappingMongoConverter() {

		DbRefResolver dbRefResolver = new DefaultDbRefResolver(
				mongoDbFactory);
		MappingMongoConverter converter = new MappingMongoConverter(
				dbRefResolver, mongoMappingContext);
		converter.setTypeMapper(
				new DefaultMongoTypeMapper(null));

		return converter;
	}
}