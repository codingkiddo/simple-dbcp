package com.simple.dbcp.pool;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

public class QueryStatistics implements LongConsumer {

	private final AtomicLong exceptionsCount = new AtomicLong(0);
    private final AtomicLong queriesCount = new AtomicLong(0);
    private final AtomicLong nanoSum = new AtomicLong(0);
    private final AtomicLong nanoMin = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong nanoMax = new AtomicLong(Long.MIN_VALUE);
    
	@Override
	public void accept(long value) {
		// TODO Auto-generated method stub
		
	}

}
