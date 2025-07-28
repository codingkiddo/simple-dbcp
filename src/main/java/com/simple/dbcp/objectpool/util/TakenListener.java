package com.simple.dbcp.objectpool.util;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TakenListener<T> implements Listener<T> {

	private final Set<T> taken;

    public TakenListener() {
        taken = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public TakenListener(int initialCapacity) {
        taken = Collections.newSetFromMap(new ConcurrentHashMap<>(initialCapacity));
    }

    @Override
    public void onTake(T object) {
        taken.add(object);
    }

    @Override
    public void onRestore(T object, boolean valid) {
        taken.remove(object);
    }

    protected T[] getTaken(T[] a) {
        return taken.toArray(a);
    }

}
