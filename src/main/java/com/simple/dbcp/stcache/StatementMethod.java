package com.simple.dbcp.stcache;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class StatementMethod {

	public interface StatementCreator { // for internal use only
		PreparedStatement newStatement(Method method, Object[] args) throws SQLException;
	}

	private final StatementCreator statementCreator;
	private final Connection rawConnection; // the underlying raw JDBC Connection
	private final Method method; // the invoked prepareStatement(...) or prepareCall(...) method
	private final Object[] args; // the invoked method args

	public StatementMethod(Connection rawConnection, StatementCreator statementCreator, Method method, Object[] args) {
		assert statementCreator != null;
		assert method != null;
		assert args != null && args.length >= 1;
		this.statementCreator = statementCreator;
		this.rawConnection = rawConnection;
		this.method = method;
		this.args = args;
	}

	Connection rawConnection() {
		return rawConnection;
	}

	PreparedStatement newStatement() throws SQLException {
		return statementCreator.newStatement(method, args);
	}

	String sqlQuery() {
		return (String) args[0]; // as only prepared and callable Statements are cached the args[0] is the query
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var that = (StatementMethod) o;
		return rawConnection == that.rawConnection // comparing with == as the JDBC Connections are pooled objects
				&& method.equals(that.method) && Arrays.equals(args, that.args);
	}

	@Override
	public int hashCode() {
		var result = rawConnection.hashCode();
		result = 31 * result + method.hashCode();
		result = 31 * result + Arrays.hashCode(args);
		return result;
	}

	@Override
	public String toString() {
		return String.format("rawConnection %s, method %s, args %s", rawConnection, method, Arrays.toString(args));
	}
}
