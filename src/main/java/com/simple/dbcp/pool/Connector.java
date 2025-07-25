package com.simple.dbcp.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface Connector {

	Connection connect() throws SQLException;
	
	final class Builder {
		private Builder() { }
	}
}