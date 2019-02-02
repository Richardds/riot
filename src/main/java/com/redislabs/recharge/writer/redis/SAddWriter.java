package com.redislabs.recharge.writer.redis;

import java.util.Map;

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.recharge.RechargeConfiguration.RedisWriterConfiguration;

@SuppressWarnings("rawtypes")
public class SAddWriter extends AbstractCollectionRedisWriter {

	public SAddWriter(RediSearchClient client, RedisWriterConfiguration writer) {
		super(client, writer, writer.getSet());
	}

	@Override
	protected void write(String key, Map record) {
		commands.sadd(key, getMemberId(record));
	}

}
