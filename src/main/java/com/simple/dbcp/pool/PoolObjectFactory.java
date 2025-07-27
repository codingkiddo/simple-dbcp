package com.simple.dbcp.pool;

public interface PoolObjectFactory<T> {
	T create();
	boolean readyToTake(T obj);
	boolean readyToRestore(T obj);
	void destroy(T obj);
}
