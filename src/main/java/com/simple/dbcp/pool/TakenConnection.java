package com.simple.dbcp.pool;

import java.sql.Connection;

public abstract class TakenConnection {

	private Connection proxyConnection = null;
	private long takenNanoTime = 0;
	private long lastAccessNanoTime = 0;

	private Thread thread = null;
	private Throwable location = null;

	TakenConnection() {
	}

	TakenConnection(TakenConnection takenConnection) {
		this.proxyConnection = takenConnection.proxyConnection;
		this.takenNanoTime = takenConnection.takenNanoTime;
		this.lastAccessNanoTime = takenConnection.lastAccessNanoTime;
		this.thread = takenConnection.thread;
		this.location = takenConnection.location;
	}

	public Connection getProxyConnection() {
		return proxyConnection;
	}

	void setProxyConnection(Connection proxyConnection) {
		this.proxyConnection = proxyConnection;
	}

	/**
	 * Returns the nano time when the Connection was taken.
	 */
	public long getTakenNanoTime() {
		return takenNanoTime;
	}

	void setTakenNanoTime(long takenNanoTime) {
		this.takenNanoTime = takenNanoTime;
	}

	/**
	 * Returns the nano time when a method was last invoked on this Connection. Only
	 * "restricted" methods invocations are updating the {@code lastAccessNanoTime},
	 * i.e., methods such as {@code close()}, {@code isClosed()} do not update the
	 * {@code lastAccessNanoTime}. See
	 * {@link org.vibur.dbcp.proxy.ConnectionInvocationHandler#restrictedInvoke
	 * restrictedInvoke()} for more details. A value of {@code 0} indicates that a
	 * restricted method was never called on this Connection proxy.
	 */
	public long getLastAccessNanoTime() {
		return lastAccessNanoTime;
	}

	void setLastAccessNanoTime(long lastAccessNanoTime) {
		this.lastAccessNanoTime = lastAccessNanoTime;
	}

	/**
	 * Returns the thread that has taken this Connection.
	 */
	public Thread getThread() {
		return thread;
	}

	void setThread(Thread thread) {
		this.thread = thread;
	}

	public Throwable getLocation() {
		return location;
	}

	void setLocation(Throwable location) {
		this.location = location;
	}

	@Override
	public String toString() {
		var currentNanoTime = System.nanoTime();
		return TakenConnection.class.getSimpleName() + '@' + Integer.toHexString(hashCode()) + '[' + proxyConnection
				+ ", takenNanoTime=" + nanosToMillis(takenNanoTime, currentNanoTime) + " ms, "
				+ (lastAccessNanoTime == 0 ? "has not been accessed"
						: "lastAccessNanoTime=" + nanosToMillis(lastAccessNanoTime, currentNanoTime) + " ms")
				+ ", thread=" + thread + (thread != null ? ", state=" + thread.getState() : "") + ']';
	}

	private static double nanosToMillis(long pastNanoTime, long currentNanoTime) {
		return (currentNanoTime - pastNanoTime) * 1e-6;
	}
}
