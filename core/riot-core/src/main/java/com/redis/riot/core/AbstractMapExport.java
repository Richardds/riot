package com.redis.riot.core;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.batch.core.Job;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.function.FunctionItemProcessor;

import com.redis.riot.core.function.RegexNamedGroupFunction;
import com.redis.riot.core.function.StructToMapFunction;
import com.redis.spring.batch.common.KeyValue;
import com.redis.spring.batch.reader.StructItemReader;

import io.lettuce.core.codec.StringCodec;

public abstract class AbstractMapExport extends AbstractExport {

	public static final Pattern DEFAULT_KEY_REGEX = Pattern.compile("\\w+:(?<id>.+)");

	private Pattern keyRegex = DEFAULT_KEY_REGEX;

	public void setKeyRegex(Pattern pattern) {
		this.keyRegex = pattern;
	}

	@Override
	protected Job job(RiotContext context) throws Exception {
		StructItemReader<String, String> reader = reader(context.getRedisContext());
		ItemProcessor<KeyValue<String>, Map<String, Object>> processor = processor(context);
		ItemWriter<Map<String, Object>> writer = writer();
		return jobBuilder().start(step(getName(), reader, processor, writer).build()).build();
	}

	protected StructItemReader<String, String> reader(RedisContext context) throws Exception {
		StructItemReader<String, String> reader = new StructItemReader<>(context.getClient(), StringCodec.UTF8);
		configureReader("export-reader", reader, context);
		return reader;
	}

	protected ItemProcessor<KeyValue<String>, Map<String, Object>> processor(RiotContext context) {
		ItemProcessor<KeyValue<String>, KeyValue<String>> processor = new FunctionItemProcessor<>(
				processor(StringCodec.UTF8, context));
		StructToMapFunction toMapFunction = new StructToMapFunction();
		if (keyRegex != null) {
			toMapFunction.setKey(new RegexNamedGroupFunction(keyRegex));
		}
		return RiotUtils.processor(processor, new FunctionItemProcessor<>(toMapFunction));
	}

	@Override
	protected boolean isStruct() {
		return true;
	}

	protected abstract ItemWriter<Map<String, Object>> writer();

}
