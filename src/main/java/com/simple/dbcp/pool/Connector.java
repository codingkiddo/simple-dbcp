package com.simple.dbcp.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.simple.dbcp.SimpleConfig;

public interface Connector {

	Connection connect() throws SQLException;

	final class Builder {
		private Builder() {
		}

		public static Connector buildConnector(SimpleConfig config, String username, String password) {
			if (config.getExternalDataSource() == null) {
				return new Driver(config, username, password);
			}
			if (username != null) {
				return new DataSourceWithCredentials(config, username, password);
			}
			return new DataSource(config);
		}

		private static final class Driver implements Connector {
			private final java.sql.Driver driver;
			private final String jdbcUrl;
			private final Properties driverProperties;

			private Driver(SimpleConfig config, String username, String password) {
				this.driver = config.getDriver();
				this.jdbcUrl = config.getJdbcUrl();

				this.driverProperties = new Properties(config.getDriverProperties());
				driverProperties.setProperty("user", username);
				driverProperties.setProperty("password", password);
			}

			@Override
			public Connection connect() throws SQLException {
				return driver.connect(jdbcUrl, driverProperties);
			}
		}

		private static final class DataSource implements Connector {
			private final javax.sql.DataSource externalDataSource;

			private DataSource(SimpleConfig config) {
				this.externalDataSource = config.getExternalDataSource();
			}

			@Override
			public Connection connect() throws SQLException {
				return externalDataSource.getConnection();
			}
		}

		private static final class DataSourceWithCredentials implements Connector {
			private final javax.sql.DataSource externalDataSource;
			private final String username;
			private final String password;

			private DataSourceWithCredentials(SimpleConfig config, String username, String password) {
				this.externalDataSource = config.getExternalDataSource();
				this.username = username;
				this.password = password;
			}

			@Override
			public Connection connect() throws SQLException {
				return externalDataSource.getConnection(username, password);
			}
		}
	}

}
