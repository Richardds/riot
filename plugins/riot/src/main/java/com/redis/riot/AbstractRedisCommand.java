package com.redis.riot;

import com.redis.riot.core.AbstractJobCommand;

import io.lettuce.core.RedisURI;
import picocli.CommandLine.ArgGroup;

public abstract class AbstractRedisCommand<C extends RedisExecutionContext> extends AbstractJobCommand<C> {

	@ArgGroup(exclusive = false)
	private RedisURIArgs redisURIArgs = new RedisURIArgs();

	@ArgGroup(exclusive = false)
	private RedisClientArgs redisClientArgs = new RedisClientArgs();

	@Override
	protected C executionContext() {
		C context = super.executionContext();
		context.setRedisContext(redisContext());
		return context;
	}

	private RedisContext redisContext() {
		RedisURI redisURI = redisURIArgs.redisURI();
		log.info("Creating Redis context with URI {} and {}", redisURI, redisClientArgs);
		RedisContext context = new RedisContext();
		context.setAutoReconnect(redisClientArgs.isAutoReconnect());
		context.setCluster(redisClientArgs.isCluster());
		context.setPoolSize(redisClientArgs.getPoolSize());
		context.setProtocolVersion(redisClientArgs.getProtocolVersion());
		context.setSslOptions(redisClientArgs.getSslArgs().sslOptions());
		context.setUri(redisURI);
		return context;
	}

	public RedisURIArgs getRedisURIArgs() {
		return redisURIArgs;
	}

	public void setRedisURIArgs(RedisURIArgs argfs) {
		this.redisURIArgs = argfs;
	}

	public RedisClientArgs getRedisClientArgs() {
		return redisClientArgs;
	}

	public void setRedisClientArgs(RedisClientArgs clientArgs) {
		this.redisClientArgs = clientArgs;
	}

}
