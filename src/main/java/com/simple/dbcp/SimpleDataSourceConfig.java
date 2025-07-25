package com.simple.dbcp;

import java.sql.Driver;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleDataSourceConfig {

	private static final Logger logger = LoggerFactory.getLogger(SimpleDataSourceConfig.class);
	
	SimpleDataSourceConfig() {}
	
	 /** The username to use when connecting to the database. */
    private String username;
    /** The password to use when connecting to the database. */
    private String password;
    
    private Driver driver = null;
    private String driverClassName = null;
    private String jdbcUrl;
    private DataSource externalDataSource = null;
}
