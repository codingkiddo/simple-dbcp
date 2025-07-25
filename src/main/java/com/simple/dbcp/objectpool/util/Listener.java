package com.simple.dbcp.objectpool.util;

public interface Listener<T> {
	void onTake(T object);
    void onRestore(T object, boolean valid);
}
