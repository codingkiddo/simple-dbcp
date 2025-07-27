package com.simple.dbcp.pool;

import java.sql.Connection;

public class ConnHolder extends TakenConnection {

	private final Connection rawConnection; // the underlying raw JDBC Connection
	private final int version; // the version of the ConnectionFactory at the moment of this ConnHolder object creation
	private long restoredNanoTime; // != 0 only when connection validation is enabled via getConnectionIdleLimitInSeconds() >= 0

	ConnHolder(Connection rawConnection, int version, long currentNanoTime) {
		assert rawConnection != null;
		this.rawConnection = rawConnection;
		this.version = version;
		this.restoredNanoTime = currentNanoTime;
	}

	ConnHolder(ConnHolder connHolder) {
		super(connHolder);
		this.rawConnection = connHolder.rawConnection;
		this.version = connHolder.version;
		this.restoredNanoTime = connHolder.restoredNanoTime;
	}

	public Connection rawConnection() {
		return rawConnection;
	}

	int version() {
		return version;
	}

	long getRestoredNanoTime() {
		return restoredNanoTime;
	}

	void setRestoredNanoTime(long restoredNanoTime) {
		this.restoredNanoTime = restoredNanoTime;
	}

	@Override
	public void setLastAccessNanoTime(long lastAccessNanoTime) {
		super.setLastAccessNanoTime(lastAccessNanoTime);
	}
}
