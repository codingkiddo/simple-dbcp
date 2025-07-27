package com.simple.dbcp.objectpool.util;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ConcurrentLinkedDequeCollection<E> implements ConcurrentCollection<E> {

	private final Deque<E> deque = new ConcurrentLinkedDeque<>();
	
	@Override
	public void offerFirst(E object) {
		deque.addFirst(object);
	}

	@Override
	public void offerLast(E object) {
		deque.add(object);
	}

	@Override
	public E pollFirst() {
		return deque.poll();
	}

	@Override
	public E pollLast() {
		return deque.pollLast();
	}

	@Override
	public int size() {
		return deque.size();
	}

}
