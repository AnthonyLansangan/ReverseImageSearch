package com.reverse.search.config;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

@Configuration
@PropertySource(value = "file:/mongo.properties")
@Profile({ "default" })
public class MongoConfig {

	@Autowired
	Environment env;

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(50);
		builder.writeConcern(WriteConcern.JOURNALED);
		builder.readPreference(ReadPreference.secondaryPreferred());
		MongoClientOptions options = builder.build();
		MongoClient mongoClient = new MongoClient(
				new ServerAddress(env.getProperty("mongo.server"), Integer.parseInt(env.getProperty("mongo.port"))),
				options);
		return mongoClient;
	}

	@Bean
	public MongoDbFactory mongoDbFactory() throws UnknownHostException {
		MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient(), env.getProperty("mongo.databaseName"));
		return mongoDbFactory;
	}

	@Bean
	public MongoTemplate mongoTemplate() throws UnknownHostException {
		MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(
				new DefaultDbRefResolver(mongoDbFactory()), new MongoMappingContext());
		mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return new MongoTemplate(mongoDbFactory(), mappingMongoConverter);
	}

}
