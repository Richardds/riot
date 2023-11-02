package com.redis.riot.core;

import java.time.Duration;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ThrottledItemWriter<T> extends AbstractItemStreamItemWriter<T> {

    private final ItemWriter<T> delegate;

    private final long sleep;

    public ThrottledItemWriter(ItemWriter<T> delegate, Duration sleep) {
        setName(ClassUtils.getShortName(getClass()));
        Assert.notNull(delegate, "Delegate must not be null");
        Assert.isTrue(RiotUtils.isPositive(sleep), "Sleep duration must be strictly positive");
        this.delegate = delegate;
        this.sleep = sleep.toMillis();
    }

    @Override
    public void open(ExecutionContext executionContext) {
        super.open(executionContext);
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).open(executionContext);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        super.update(executionContext);
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).update(executionContext);
        }
    }

    @Override
    public void close() {
        super.close();
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).close();
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (delegate instanceof ItemStreamSupport) {
            ((ItemStreamSupport) delegate).setName(name);
        }
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        delegate.write(items);
        Thread.sleep(sleep);
    }

}
