package com.simple.dbcp.pool;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface Hook {

	interface InitConnection extends Hook {
		void on(Connection rawConnection, long takenNanos) throws SQLException;
	}
	
	interface GetConnection extends Hook {
		void on(Connection rawConnection, long takenNanos) throws SQLException;
	}
	
	interface CloseConnection extends Hook {
        void on(Connection rawConnection, long takenNanos) throws SQLException;
	}
	
	interface DestroyConnection extends Hook {
        void on(Connection rawConnection, long takenNanos);
    }

    interface GetConnectionTimeout extends Hook {
        void on(TakenConnection[] takenConnections, long takenNanos);
    }

    interface MethodInvocation extends Hook {
        void on(Object proxy, Method method, Object[] args) throws SQLException;
    }

    interface StatementExecution extends Hook {
    	Object on(Statement proxy, Method method, Object[] args, String sqlQuery, List<Object[]> sqlQueryParams,
                StatementProceedingPoint proceed) throws SQLException;
    }
    
    interface StatementProceedingPoint extends StatementExecution { }

    interface ResultSetRetrieval extends Hook {
        void on(String sqlQuery, List<Object[]> sqlQueryParams, long resultSetSize, long resultSetNanoTime);
    }

}
