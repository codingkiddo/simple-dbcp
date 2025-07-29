package com.simple.dbcp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.dbcp.SimpleConfig;
import com.simple.dbcp.SimpleDBCPException;

public class JdbcUtils {

	private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

	private JdbcUtils() {
	}

	public static void initLoginTimeout(SimpleConfig config) throws SimpleDBCPException {
		var loginTimeout = config.getLoginTimeoutInSeconds();
		if (config.getExternalDataSource() == null) {
			DriverManager.setLoginTimeout(loginTimeout);
		} else {
			try {
				config.getExternalDataSource().setLoginTimeout(loginTimeout);
			} catch (SQLException e) {
				throw new SimpleDBCPException(e);
			}
		}
	}

	public static void setDefaultValues(Connection rawConnection, SimpleConfig config) throws SQLException {
		if (config.getDefaultAutoCommit() != null) {
			rawConnection.setAutoCommit(config.getDefaultAutoCommit());
		}
		if (config.getDefaultReadOnly() != null) {
			rawConnection.setReadOnly(config.getDefaultReadOnly());
		}
		if (config.getDefaultTransactionIsolationIntValue() != null) {
			// noinspection MagicConstant - the int value is checked/ set during Vibur
			// config validation
			rawConnection.setTransactionIsolation(config.getDefaultTransactionIsolationIntValue());
		}
		if (config.getDefaultCatalog() != null) {
			rawConnection.setCatalog(config.getDefaultCatalog());
		}
	}

	public static boolean validateOrInitialize(Connection rawConnection, String sqlQuery, SimpleConfig config) {
		if (sqlQuery == null) {
			return true;
		}

		try {
			if (sqlQuery.equals(SimpleConfig.IS_VALID_QUERY)) {
				return rawConnection.isValid(config.getValidateTimeoutInSeconds());
			}

			executeSqlQuery(rawConnection, sqlQuery, config);
			return true;
		} catch (SQLException e) {
			logger.debug("Couldn't validate/ initialize rawConnection {}", rawConnection, e);
			return false;
		}
	}

	private static void executeSqlQuery(Connection rawConnection, String sqlQuery, SimpleConfig config)
			throws SQLException {
		var oldTimeout = setNetworkTimeoutIfDifferent(rawConnection, config);

		Statement rawStatement = null;
		try {
			rawStatement = rawConnection.createStatement();
			rawStatement.setQueryTimeout(config.getValidateTimeoutInSeconds());
			rawStatement.execute(sqlQuery);
		} finally {
			quietClose(rawStatement);
		}

		resetNetworkTimeout(rawConnection, config.getNetworkTimeoutExecutor(), oldTimeout);
	}

	private static int setNetworkTimeoutIfDifferent(Connection rawConnection, SimpleConfig config) throws SQLException {
		if (config.isUseNetworkTimeout()) {
			var newTimeout = (int) TimeUnit.SECONDS.toMillis(config.getValidateTimeoutInSeconds());
			var oldTimeout = rawConnection.getNetworkTimeout();
			if (newTimeout != oldTimeout) {
				rawConnection.setNetworkTimeout(config.getNetworkTimeoutExecutor(), newTimeout);
				return oldTimeout;
			}
		}
		return -1;
	}

	private static void resetNetworkTimeout(Connection rawConnection, Executor executor, int oldTimeout)
			throws SQLException {
		if (oldTimeout >= 0) {
			rawConnection.setNetworkTimeout(executor, oldTimeout);
		}
	}

	public static void clearWarnings(Connection connection) throws SQLException {
		if (connection != null) {
			connection.clearWarnings();
		}
	}

	public static void clearWarnings(PreparedStatement preparedStatement) throws SQLException {
		if (preparedStatement != null) {
			preparedStatement.clearWarnings();
		}
	}

	public static void quietClose(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			logger.warn("Couldn't close {}", connection, e);
		}
	}

	public static void quietClose(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			logger.warn("Couldn't close {}", statement, e);
		}
	}

	public static void quietClose(ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			logger.warn("Couldn't close {}", resultSet, e);
		}
	}

	public static SQLException chainSQLException(SQLException main, SQLException next) {
		if (main == null) {
			return next;
		}
		main.setNextException(next);
		return main;
	}
}
