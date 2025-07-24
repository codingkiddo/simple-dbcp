package com.simple.dbcp;

import javax.sql.DataSource;

public interface SimpleDataSource extends DataSource, AutoCloseable {

	enum State {
		NEW, WORKING, TERMINATED
	}
}
