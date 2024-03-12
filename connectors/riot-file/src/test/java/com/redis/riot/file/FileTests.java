package com.redis.riot.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.redis.riot.core.RedisOptions;
import com.redis.riot.core.operation.HsetBuilder;
import com.redis.spring.batch.test.AbstractTestBase;

abstract class FileTests extends AbstractTestBase {

	public static final String BEERS_JSON_URL = "https://storage.googleapis.com/jrx/beers.json";

	private static final String ID = "id";

	private static final String keyspace = "beer";

	@SuppressWarnings("unchecked")
	@Test
	void fileImportJSON() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
		FileImport executable = new FileImport();
		executable.setRedisOptions(redisClientOptions());
		executable.setFiles(BEERS_JSON_URL);
		HsetBuilder hsetBuilder = new HsetBuilder();
		hsetBuilder.setKeyspace(keyspace);
		hsetBuilder.setKeyFields(ID);
		executable.setOperations(hsetBuilder.build());
		executable.run();
		List<String> keys = commands.keys("*");
		assertEquals(216, keys.size());
		for (String key : keys) {
			Map<String, String> map = commands.hgetall(key);
			String id = map.get(ID);
			assertEquals(key, keyspace + ":" + id);
		}
		Map<String, String> beer1 = commands.hgetall(keyspace + ":1");
		Assertions.assertEquals("Hocus Pocus", beer1.get("name"));
	}

	private RedisOptions redisClientOptions() {
		RedisOptions options = new RedisOptions();
		options.setCluster(getRedisServer().isRedisCluster());
		options.setUri(getRedisServer().getRedisURI());
		return options;
	}

	@SuppressWarnings("unchecked")
	@Test
	void fileApiImportCSV() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
		FileImport executable = new FileImport();
		executable.setRedisOptions(redisClientOptions());
		executable.setFiles("https://storage.googleapis.com/jrx/beers.csv");
		executable.setHeader(true);
		HsetBuilder hsetBuilder = new HsetBuilder();
		hsetBuilder.setKeyspace(keyspace);
		hsetBuilder.setKeyFields(ID);
		executable.setOperations(hsetBuilder.build());
		executable.run();
		List<String> keys = commands.keys("*");
		assertEquals(2410, keys.size());
		for (String key : keys) {
			Map<String, String> map = commands.hgetall(key);
			String id = map.get(ID);
			assertEquals(key, keyspace + ":" + id);
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	void fileApiFileExpansion() throws IOException {
		Path temp = Files.createTempDirectory("fileExpansion");
		File file1 = temp.resolve("beers1.csv").toFile();
		org.apache.commons.io.FileUtils
				.copyInputStreamToFile(getClass().getClassLoader().getResourceAsStream("beers1.csv"), file1);
		File file2 = temp.resolve("beers2.csv").toFile();
		org.apache.commons.io.FileUtils
				.copyInputStreamToFile(getClass().getClassLoader().getResourceAsStream("beers2.csv"), file2);
		FileImport executable = new FileImport();
		executable.setRedisOptions(redisClientOptions());
		executable.setFiles(temp.resolve("*.csv").toFile().getPath());
		executable.setHeader(true);
		HsetBuilder hsetBuilder = new HsetBuilder();
		hsetBuilder.setKeyspace(keyspace);
		hsetBuilder.setKeyFields(ID);
		executable.setOperations(hsetBuilder.build());
		executable.run();
		List<String> keys = commands.keys("*");
		assertEquals(2410, keys.size());
		for (String key : keys) {
			Map<String, String> map = commands.hgetall(key);
			String id = map.get(ID);
			assertEquals(key, keyspace + ":" + id);
		}
	}

}
