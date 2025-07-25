package com.simple.dbcp.stcache;

import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

public class StatementHolder {
	public enum State {
		AVAILABLE, IN_USE, EVICTED
	}

	private final Statement rawStatement; // the underlying raw JDBC Statement
	private final AtomicReference<State> state; // a null value means that this StatementHolder instance is not included
												// in the cache

	private String sqlQuery;

	public StatementHolder(Statement rawStatement, AtomicReference<State> state, String sqlQuery) {
		assert rawStatement != null;
		this.rawStatement = rawStatement;
		this.state = state;
		this.sqlQuery = sqlQuery;
	}

	public Statement rawStatement() {
		return rawStatement;
	}

	public AtomicReference<State> state() {
		return state;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
}
