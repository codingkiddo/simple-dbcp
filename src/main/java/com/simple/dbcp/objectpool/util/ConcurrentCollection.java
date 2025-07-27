package com.simple.dbcp.objectpool.util;

public interface ConcurrentCollection<E> {

	 void offerFirst(E object);
	 void offerLast(E object);
	 E pollFirst();
	 E pollLast();
	 int size();
	 
}
