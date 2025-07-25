package com.simple.dbcp.stcache;

import java.sql.SQLException;

public interface StatementCache {
	StatementHolder take(StatementMethod statementMethod) throws SQLException;
}
