package com.simple.dbcp.objectpool;

import java.util.concurrent.TimeUnit;

import com.simple.dbcp.objectpool.util.Listener;

public interface PoolService<T> extends BasePool {

	T take();
	T take(long[] waitedNanos);
	T takeUninterruptibly();
	T takeUninterruptibly(long[] waitedNanos);
	T tryTake(long timeout, TimeUnit unit);
	T tryTake(long timeout, TimeUnit unit, long[] waitedNanos);
	T tryTake();
	void restore(T object);
	void restore(T object, boolean valid);
	Listener<T> listener();
	boolean isFair();
	
}
