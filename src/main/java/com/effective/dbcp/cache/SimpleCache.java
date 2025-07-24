package com.effective.dbcp.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleCache<K, V> {

	private final ConcurrentHashMap<K, CacheItem<V>> cache = new ConcurrentHashMap<>();
	private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
	private final long ttlMillis;

	public SimpleCache(long ttlSeconds) {
        this.ttlMillis = ttlSeconds * 1000;
        startCleanupTask();
    }

	public void put(K key, V value) {
		long expiryTime = System.currentTimeMillis() + this.ttlMillis;
		cache.put(key, new CacheItem<>(value, expiryTime));
	}
	
	public V get(K key) {
		CacheItem<V> item = cache.get(key);
		if ( key == null || item.isExpired()) {
			cache.remove(key);
			return null;
		}
		return item.value;
	}
	
	public void remove(K key) {
		cache.remove(key);
	}
	
	public int size() {
        return cache.size();
    }
	
	private void startCleanupTask() {
		cleaner.scheduleAtFixedRate(() -> {
			long now = System.currentTimeMillis();
			for ( Map.Entry<K, CacheItem<V>> entry : cache.entrySet() ) {
				if ( entry.getValue().expiryTime < now ) {
					cache.remove(entry.getKey());
				}
			}
		}, ttlMillis, ttlMillis, TimeUnit.MILLISECONDS);
	}
	
	private static class CacheItem<V> {
		V value; 
		long expiryTime;
		
		CacheItem(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
		
		boolean isExpired() {
			return System.currentTimeMillis() > expiryTime;
		}
	}
	
	public void shutdown() {
        cleaner.shutdown();
    }

}


