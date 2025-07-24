package com.simple.dbcp;

import javax.sql.DataSource;

public interface SimpleDataSource extends DataSource, AutoCloseable {

	enum State {
		NEW, WORKING, TERMINATED
	}
	
	void start() throws SimpleDBCPException;
	State getState();
	void terminate();
}
