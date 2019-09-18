package com.redislabs.riot.cli.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redislabs.riot.redis.reader.RedisItemReader;

import picocli.CommandLine.Option;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

public class RedisReaderOptions {

	private final Logger log = LoggerFactory.getLogger(RedisReaderOptions.class);

	@Option(names = "--count", description = "Number of elements to return for each scan call", paramLabel = "<int>")
	private Integer count;
	@Option(names = "--scan-key-separator", description = "Redis key separator (default: ${DEFAULT-VALUE})", paramLabel = "<string>")
	private String separator = ":";
	@Option(names = { "--scan-keyspace" }, description = "Redis keyspace prefix", paramLabel = "<string>")
	private String keyspace;
	@Option(names = { "--scan-keys" }, arity = "1..*", description = "Key fields", paramLabel = "<names>")
	private String[] keys = new String[0];

	public RedisItemReader reader(Pool<Jedis> jedisPool) {
		String scanPattern = scanPattern();
		log.debug("Creating Redis reader with match={} and count={}", scanPattern, count);
		RedisItemReader reader = new RedisItemReader(jedisPool);
		reader.setCount(count);
		reader.setMatch(scanPattern);
		reader.setKeys(keys);
		reader.setKeyspace(keyspace);
		reader.setSeparator(separator);
		return reader;
	}

	public String scanPattern() {
		if (keyspace == null) {
			return null;
		}
		return keyspace + separator + "*";
	}

}
