package com.simple.dbcp.pool;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

public class QueryStatistics implements LongConsumer {

	private final AtomicLong exceptionsCount = new AtomicLong(0);
	private final AtomicLong queriesCount = new AtomicLong(0);
	private final AtomicLong nanoSum = new AtomicLong(0);
	private final AtomicLong nanoMin = new AtomicLong(Long.MAX_VALUE);
	private final AtomicLong nanoMax = new AtomicLong(Long.MIN_VALUE);

	@Override
	public void accept(long nanoTime) {
		queriesCount.incrementAndGet();
		nanoSum.addAndGet(nanoTime);
		while (nanoTime < nanoMin.get()) {
			nanoMin.getAndSet(nanoTime);
		}
		while (nanoTime > nanoMax.get()) {
			nanoMax.getAndSet(nanoTime);
		}
	}

	public void incrementExceptions() {
		exceptionsCount.incrementAndGet();
	}

	public void combine(QueryStatistics other) {
		exceptionsCount.addAndGet(other.getExceptionsCount());
		queriesCount.addAndGet(other.getQueriesCount());
		nanoSum.addAndGet(other.getSum().toNanos());
		while (other.getMin().toNanos() < nanoMin.get()) {
			nanoMin.getAndSet(other.getMin().toNanos());
		}
		while (other.getMax().toNanos() > nanoMax.get()) {
			nanoMax.getAndSet(other.getMax().toNanos());
		}
	}

	public void reset() {
		while (exceptionsCount.get() != 0 || queriesCount.get() != 0 || nanoSum.get() != 0
				|| nanoMin.get() != Long.MAX_VALUE || nanoMax.get() != Long.MIN_VALUE) {
			exceptionsCount.getAndSet(0);
			queriesCount.getAndSet(0);
			nanoSum.getAndSet(0);
			nanoMin.getAndSet(Long.MAX_VALUE);
			nanoMax.getAndSet(Long.MIN_VALUE);
		}
	}

	public long getExceptionsCount() {
		return exceptionsCount.get();
	}

	public long getQueriesCount() {
		return queriesCount.get();
	}

	public Duration getSum() {
		return Duration.ofNanos(nanoSum.get());
	}

	public Duration getMin() {
		return Duration.ofNanos(nanoMin.get());
	}

	public Duration getMax() {
		return Duration.ofNanos(nanoMax.get());
	}

	public double getAverage() {
		var count = queriesCount.get();
		return count > 0 ? (double) getSum().toNanos() / count : 0.0d;
	}

	@Override
	public String toString() {
		return String.format("{exceptions = %d, queries = %d, sum = %f ms, min = %f ms, average = %f ms, max = %f ms}",
				getExceptionsCount(), getQueriesCount(), getSum().toNanos() * 1e-6, getMin().toNanos() * 1e-6,
				getAverage() * 1e-6, getMax().toNanos() * 1e-6);
	}
}
