package com.redis.riot.core;

import com.redis.spring.batch.RedisItemWriter;
import com.redis.spring.batch.common.KeyValue;
import com.redis.spring.batch.writer.StructItemWriter;

import io.lettuce.core.codec.StringCodec;

public abstract class AbstractStructImport extends AbstractJobRunnable {

	private RedisWriterOptions writerOptions = new RedisWriterOptions();

	public void setWriterOptions(RedisWriterOptions options) {
		this.writerOptions = options;
	}

	protected RedisItemWriter<String, String, KeyValue<String>> writer() {
		StructItemWriter<String, String> writer = new StructItemWriter<>(getRedisClient(), StringCodec.UTF8);
		return writer(writer, writerOptions);
	}

}
