package com.simple.dbcp.objectpool.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.dbcp.objectpool.BasePool;

public class SamplingPoolReducer implements ThreadedPoolReducer {

	private static final Logger logger = LoggerFactory.getLogger(SamplingPoolReducer.class);

	private final BasePool pool;
	private final long sleepNanoTime;
	private final int samples;

	private final Thread reducerThread;

	protected static final double MAX_REDUCTION_FRACTION = 0.2;
	protected int minRemainingCreated;

	public SamplingPoolReducer(BasePool pool, long timeInterval, TimeUnit unit, int samples) {
		ArgumentValidation.forbidIllegalArgument(timeInterval <= 0, "timeInterval");
		ArgumentValidation.forbidIllegalArgument(samples <= 0, "samples");

		this.sleepNanoTime = unit.toNanos(timeInterval) / samples;
		ArgumentValidation.forbidIllegalArgument(sleepNanoTime == 0, "sleepNanoTime");

		this.pool = Objects.requireNonNull(pool);
		this.samples = samples;

		this.reducerThread = new Thread(new PoolReducerRunnable());
	}

	@Override
	public void start() {
		reducerThread.setName(getThreadName());
		reducerThread.setDaemon(true);
		reducerThread.setPriority(Thread.MAX_PRIORITY - 2);
		reducerThread.start();
	}

	protected String getThreadName() {
		return reducerThread.getName();
	}

	private class PoolReducerRunnable implements Runnable {
		@Override
		public void run() {
			var sample = 1;
			minRemainingCreated = Integer.MAX_VALUE;
			for (;;) {
				try {
					TimeUnit.NANOSECONDS.sleep(sleepNanoTime);
					samplePool();
					if (sample++ % samples == 0) {
						reducePool();
						sample = 1;
						minRemainingCreated = Integer.MAX_VALUE;
					}
				} catch (InterruptedException ignored) {
					break;
				}
			}
		}
	}

	protected void samplePool() {
		var remainingCreated = pool.remainingCreated();
		minRemainingCreated = Math.min(minRemainingCreated, remainingCreated);
	}

	protected void reducePool() {
		var reduction = calculateReduction();

		var reduced = -1;
		Throwable thrown = null;
		try {
			reduced = pool.reduceCreatedBy(reduction, false);
		} catch (Throwable t) { // equivalent to catching "RuntimeException | Error", however, better for Kotlin
								// interoperability
			thrown = t;
		} finally {
			afterReduce(reduction, reduced, thrown);
		}
	}

	protected int calculateReduction() {
		var createdTotal = pool.createdTotal();
		var maxReduction = (int) Math.ceil(createdTotal * MAX_REDUCTION_FRACTION);
		var reduction = Math.min(minRemainingCreated, maxReduction);
		var bottomThreshold = createdTotal - pool.initialSize();
		reduction = Math.min(reduction, bottomThreshold);
		return Math.max(reduction, 0);
	}

	protected void afterReduce(int reduction, int reduced, Throwable thrown) {
		if (thrown != null) {
			terminate();
		}
	}

	@Override
	public Thread.State getState() {
		return reducerThread.getState();
	}

	@Override
	public void terminate() {
		reducerThread.interrupt();
	}

}
