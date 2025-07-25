package com.simple.dbcp.objectpool;

public interface BasePool extends AutoCloseable {

	int taken();
	int remainingCreated();
	int createdTotal();
	int remainingCapacity();
	int initialSize();
	int maxSize();
	int reduceCreatedBy(int reduceBy, boolean ignoreInitialSize);
	int reduceCreatedTo(int reduceTo, boolean ignoreInitialSize);
	int drainCreated();
	void terminated();
	boolean isTerminated();
	
}
