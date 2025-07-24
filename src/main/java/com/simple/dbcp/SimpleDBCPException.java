package com.simple.dbcp;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDBCPException extends RuntimeException {

	private static final long serialVersionUID = 8219109369199360932L;

	private static final Logger logger = LoggerFactory.getLogger(SimpleDBCPException.class);

	public SimpleDBCPException() {
        super();
    }

    public SimpleDBCPException(String message) {
        super(message);
    }

    public SimpleDBCPException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleDBCPException(Throwable cause) {
        super(cause);
    }

    public SQLException unwrapSQLException() {
        var cause = getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }

        logger.error("Unexpected exception cause", this);
        throw this; 
    }
}
