package com.simple.dbcp;

import java.sql.Driver;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.dbcp.pool.Connector;

public class SimpleConfig {

	private static final Logger logger = LoggerFactory.getLogger(SimpleConfig.class);
	
	public static final String DEFAULT_PROPERTIES_CONFIG_FILE_NAME = "simple-dbcp-config.properties";
    public static final String DEFAULT_XML_CONFIG_FILE_NAME = "simple-dbcp-config.xml";

    public static final String SQLSTATE_POOL_NOTSTARTED_ERROR = "VI000";
    public static final String SQLSTATE_POOL_CLOSED_ERROR     = "VI001";
    public static final String SQLSTATE_TIMEOUT_ERROR         = "VI002";
    public static final String SQLSTATE_CONN_INIT_ERROR       = "VI003";
    public static final String SQLSTATE_INTERRUPTED_ERROR     = "VI004";
    public static final String SQLSTATE_OBJECT_CLOSED_ERROR   = "VI005";
    public static final String SQLSTATE_WRAPPER_ERROR         = "VI006";

    static final int STATEMENT_CACHE_MAX_SIZE = 2000;
    
    SimpleConfig() { }
    
    private String username;
    private String password;

    private Driver driver = null;
    private Properties driverProperties = null;
    private String driverClassName = null;
    private String jdbcUrl;
    private DataSource externalDataSource = null;
    private Connector connector = null;

    private int connectionIdleLimitInSeconds = 5;
    private int validateTimeoutInSeconds = 3;
    public static final String IS_VALID_QUERY = "isValid";
    private String testConnectionQuery = IS_VALID_QUERY;
    private String initSQL = null;

    private boolean useNetworkTimeout = false;
    private Executor networkTimeoutExecutor = null;

    private int poolInitialSize = 5;
    private int poolMaxSize = 50;
    private boolean poolFair = true;
    private boolean poolEnableConnectionTracking = false;

    private PoolService<ConnHolder> pool = null;
    private ConcurrentCollection<ConnHolder> concurrentCollection = new ConcurrentLinkedDequeCollection<>();
    private ViburObjectFactory connectionFactory = null;
    private TakenConnectionsFormatter takenConnectionsFormatter = null;
    private ThreadedPoolReducer poolReducer = null;


    private String poolReducerClass = PoolReducer.class.getName();
    private int reducerTimeIntervalInSeconds = 30;
    private int reducerSamples = 15;

    private boolean allowConnectionAfterTermination = false;
    private boolean allowUnwrapping = true;

    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    private final String defaultName = "p" + idGenerator.getAndIncrement();

    private String name = defaultName;

    private boolean enableJMX = true;

    private long connectionTimeoutInMs = 15_000;
    private int loginTimeoutInSeconds = 5;
    private long acquireRetryDelayInMs = 500;
    private int acquireRetryAttempts = 3;

    private int statementCacheMaxSize = 0;
    private StatementCache statementCache = null;


    private String criticalSQLStates = "08001,08006,08007,08S01,57P01,57P02,57P03,JZ0C0,JZ0C1";
    private long logConnectionLongerThanMs = 3000;
    private boolean logStackTraceForLongConnection = false;
    private long logQueryExecutionLongerThanMs = 3000;
    private boolean logStackTraceForLongQueryExecution = false;
    private int collectQueryStatistics = 100;
    private final QueryStatistics queryStatistics = new QueryStatistics();
    private long logLargeResultSet = 500;
    private boolean logStackTraceForLargeResultSet = false;
    private boolean includeQueryParameters = true;

    private boolean logTakenConnectionsOnTimeout = false;
    private boolean logAllStackTracesOnTimeout = false;

    private Pattern logLineRegex = null;
    private boolean resetDefaultsAfterUse = false;
    private Boolean defaultAutoCommit;
    private Boolean defaultReadOnly;
    private String defaultTransactionIsolation;
    private String defaultCatalog;
    private Integer defaultTransactionIsolationIntValue;

    private boolean clearSQLWarnings = false;

    private final ConnHooks connHooks = newConnHooks();
    private final InvocationHooks invocationHooks = newInvocationHooks();
}
