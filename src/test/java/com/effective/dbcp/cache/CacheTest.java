package com.effective.dbcp.cache;

public class CacheTest {
	public static void main(String[] args) throws InterruptedException {
        SimpleCache<String, String> cache = new SimpleCache<>(2); // 2 second TTL

        cache.put("hello", "world");
        System.out.println("Immediately: " + cache.get("hello")); // world

        Thread.sleep(2500);
        System.out.println("After 2.5 seconds: " + cache.get("hello")); // null

        cache.shutdown();
    }

}
