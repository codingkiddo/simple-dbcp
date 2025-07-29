package com.simple.dbcp;

import java.sql.Driver;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.dbcp.objectpool.PoolService;
import com.simple.dbcp.objectpool.util.ConcurrentCollection;
import com.simple.dbcp.objectpool.util.ConcurrentLinkedDequeCollection;
import com.simple.dbcp.objectpool.util.ThreadedPoolReducer;
import com.simple.dbcp.pool.ConnHolder;
import com.simple.dbcp.pool.Connector;
import com.simple.dbcp.pool.HookHolder.ConnHooks;
import com.simple.dbcp.pool.HookHolder.InvocationHooks;
import com.simple.dbcp.pool.PoolReducer;
import com.simple.dbcp.pool.QueryStatistics;
import com.simple.dbcp.pool.SimpleDBCPObjectFactory;
import com.simple.dbcp.pool.TakenConnectionsFormatter;
import com.simple.dbcp.stcache.StatementCache;

public class SimpleConfig {

	private static final Logger logger = LoggerFactory.getLogger(SimpleConfig.class);
	
	public static final String DEFAULT_PROPERTIES_CONFIG_FILE_NAME = "simple-dbcp-config.properties";
    public static final String DEFAULT_XML_CONFIG_FILE_NAME = "simple-dbcp-config.xml";

    public static final String SQLSTATE_POOL_NOTSTARTED_ERROR = "E000";
    public static final String SQLSTATE_POOL_CLOSED_ERROR     = "E001";
    public static final String SQLSTATE_TIMEOUT_ERROR         = "E002";
    public static final String SQLSTATE_CONN_INIT_ERROR       = "E003";
    public static final String SQLSTATE_INTERRUPTED_ERROR     = "E004";
    public static final String SQLSTATE_OBJECT_CLOSED_ERROR   = "E005";
    public static final String SQLSTATE_WRAPPER_ERROR         = "E006";

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
    private SimpleDBCPObjectFactory connectionFactory = null;
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
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public DataSource getExternalDataSource() {
        return externalDataSource;
    }

    public void setExternalDataSource(DataSource externalDataSource) {
        this.externalDataSource = externalDataSource;
    }

    public Connector getConnector() {
        return connector;
    }

    protected void setConnector(Connector connector) {
        this.connector = connector;
    }

    public int getConnectionIdleLimitInSeconds() {
        return connectionIdleLimitInSeconds;
    }

    public void setConnectionIdleLimitInSeconds(int connectionIdleLimitInSeconds) {
        this.connectionIdleLimitInSeconds = connectionIdleLimitInSeconds;
    }

    public int getValidateTimeoutInSeconds() {
        return validateTimeoutInSeconds;
    }

    public void setValidateTimeoutInSeconds(int validateTimeoutInSeconds) {
        this.validateTimeoutInSeconds = validateTimeoutInSeconds;
    }

    public String getTestConnectionQuery() {
        return testConnectionQuery;
    }

    public void setTestConnectionQuery(String testConnectionQuery) {
        this.testConnectionQuery = testConnectionQuery;
    }

    public String getInitSQL() {
        return initSQL;
    }

    public void setInitSQL(String initSQL) {
        this.initSQL = initSQL;
    }

    public boolean isUseNetworkTimeout() {
        return useNetworkTimeout;
    }

    public void setUseNetworkTimeout(boolean useNetworkTimeout) {
        this.useNetworkTimeout = useNetworkTimeout;
    }

    public Executor getNetworkTimeoutExecutor() {
        return networkTimeoutExecutor;
    }

    public void setNetworkTimeoutExecutor(Executor networkTimeoutExecutor) {
        this.networkTimeoutExecutor = networkTimeoutExecutor;
    }

    public int getPoolInitialSize() {
        return poolInitialSize;
    }

    public void setPoolInitialSize(int poolInitialSize) {
        this.poolInitialSize = poolInitialSize;
    }

    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    public void setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
    }

    public boolean isPoolFair() {
        return poolFair;
    }

    public void setPoolFair(boolean poolFair) {
        this.poolFair = poolFair;
    }

    public boolean isPoolEnableConnectionTracking() {
        return poolEnableConnectionTracking;
    }

    public void setPoolEnableConnectionTracking(boolean poolEnableConnectionTracking) {
        this.poolEnableConnectionTracking = poolEnableConnectionTracking;
    }

    public PoolService<ConnHolder> getPool() {
        return pool;
    }

    protected void setPool(PoolService<ConnHolder> pool) {
        this.pool = pool;
    }

    protected ConcurrentCollection<ConnHolder> getConcurrentCollection() {
        return concurrentCollection;
    }

    protected void setConcurrentCollection(ConcurrentCollection<ConnHolder> concurrentCollection) {
        this.concurrentCollection = concurrentCollection;
    }

    protected SimpleDBCPObjectFactory getConnectionFactory() {
        return connectionFactory;
    }

    protected void setConnectionFactory(SimpleDBCPObjectFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public TakenConnectionsFormatter getTakenConnectionsFormatter() {
        return takenConnectionsFormatter;
    }

    public void setTakenConnectionsFormatter(TakenConnectionsFormatter takenConnectionsFormatter) {
        this.takenConnectionsFormatter = takenConnectionsFormatter;
    }

    protected ThreadedPoolReducer getPoolReducer() {
        return poolReducer;
    }

    protected void setPoolReducer(ThreadedPoolReducer poolReducer) {
        this.poolReducer = poolReducer;
    }

    protected String getPoolReducerClass() {
        return poolReducerClass;
    }

    protected void setPoolReducerClass(String poolReducerClass) {
        this.poolReducerClass = poolReducerClass;
    }

    public int getReducerTimeIntervalInSeconds() {
        return reducerTimeIntervalInSeconds;
    }

    public void setReducerTimeIntervalInSeconds(int reducerTimeIntervalInSeconds) {
        this.reducerTimeIntervalInSeconds = reducerTimeIntervalInSeconds;
    }

    public int getReducerSamples() {
        return reducerSamples;
    }

    public void setReducerSamples(int reducerSamples) {
        this.reducerSamples = reducerSamples;
    }

    public boolean isAllowConnectionAfterTermination() {
        return allowConnectionAfterTermination;
    }

    public void setAllowConnectionAfterTermination(boolean allowConnectionAfterTermination) {
        this.allowConnectionAfterTermination = allowConnectionAfterTermination;
    }

    public boolean isAllowUnwrapping() {
        return allowUnwrapping;
    }

    public void setAllowUnwrapping(boolean allowUnwrapping) {
        this.allowUnwrapping = allowUnwrapping;
    }

    public String getName() {
        return name;
    }

    /**
     * <b>NOTE:</b> the pool name can be set only once; pool renaming is not supported.
     *
     * @param name the pool name to use
     */
    public void setName(String name) {
        if (name == null || (name = name.trim()).isEmpty()) {
            logger.error("Invalid pool name {}", name);
            return;
        }
        if (!defaultName.equals(this.name) || defaultName.equals(name)) {
            logger.error("Pool name is already set or duplicated, existing name = {}, incoming name = {}", this.name, name);
            return;
        }
        this.name = name;
    }

    public String getJmxName() {
        return "org.vibur.dbcp:type=ViburDBCP-" + name;
    }

    public boolean isEnableJMX() {
        return enableJMX;
    }

    public void setEnableJMX(boolean enableJMX) {
        this.enableJMX = enableJMX;
    }

    public long getConnectionTimeoutInMs() {
        return connectionTimeoutInMs;
    }

    public void setConnectionTimeoutInMs(long connectionTimeoutInMs) {
        this.connectionTimeoutInMs = connectionTimeoutInMs;
    }

    public int getLoginTimeoutInSeconds() {
        return loginTimeoutInSeconds;
    }

    public void setLoginTimeoutInSeconds(int loginTimeoutInSeconds) {
        this.loginTimeoutInSeconds = loginTimeoutInSeconds;
    }

    public long getAcquireRetryDelayInMs() {
        return acquireRetryDelayInMs;
    }

    public void setAcquireRetryDelayInMs(long acquireRetryDelayInMs) {
        this.acquireRetryDelayInMs = acquireRetryDelayInMs;
    }

    public int getAcquireRetryAttempts() {
        return acquireRetryAttempts;
    }

    public void setAcquireRetryAttempts(int acquireRetryAttempts) {
        this.acquireRetryAttempts = acquireRetryAttempts;
    }

    public int getStatementCacheMaxSize() {
        return statementCacheMaxSize;
    }

    public void setStatementCacheMaxSize(int statementCacheMaxSize) {
        this.statementCacheMaxSize = statementCacheMaxSize;
    }

    public StatementCache getStatementCache() {
        return statementCache;
    }

    protected void setStatementCache(StatementCache statementCache) {
        this.statementCache = statementCache;
    }

    public String getCriticalSQLStates() {
        return criticalSQLStates;
    }

    public void setCriticalSQLStates(String criticalSQLStates) {
        this.criticalSQLStates = criticalSQLStates;
    }

    public long getLogConnectionLongerThanMs() {
        return logConnectionLongerThanMs;
    }

    public void setLogConnectionLongerThanMs(long logConnectionLongerThanMs) {
        this.logConnectionLongerThanMs = logConnectionLongerThanMs;
    }

    public boolean isLogStackTraceForLongConnection() {
        return logStackTraceForLongConnection;
    }

    public void setLogStackTraceForLongConnection(boolean logStackTraceForLongConnection) {
        this.logStackTraceForLongConnection = logStackTraceForLongConnection;
    }

    public long getLogQueryExecutionLongerThanMs() {
        return logQueryExecutionLongerThanMs;
    }

    public void setLogQueryExecutionLongerThanMs(long logQueryExecutionLongerThanMs) {
        this.logQueryExecutionLongerThanMs = logQueryExecutionLongerThanMs;
    }

    public boolean isLogStackTraceForLongQueryExecution() {
        return logStackTraceForLongQueryExecution;
    }

    public void setLogStackTraceForLongQueryExecution(boolean logStackTraceForLongQueryExecution) {
        this.logStackTraceForLongQueryExecution = logStackTraceForLongQueryExecution;
    }

    public int getCollectQueryStatistics() {
        return collectQueryStatistics;
    }

    public void setCollectQueryStatistics(int collectQueryStatistics) {
        this.collectQueryStatistics = collectQueryStatistics;
    }

    public QueryStatistics getQueryStatistics() {
        return queryStatistics;
    }

    public long getLogLargeResultSet() {
        return logLargeResultSet;
    }

    public void setLogLargeResultSet(long logLargeResultSet) {
        this.logLargeResultSet = logLargeResultSet;
    }

    public boolean isLogStackTraceForLargeResultSet() {
        return logStackTraceForLargeResultSet;
    }

    public void setLogStackTraceForLargeResultSet(boolean logStackTraceForLargeResultSet) {
        this.logStackTraceForLargeResultSet = logStackTraceForLargeResultSet;
    }

    public boolean isIncludeQueryParameters() {
        return includeQueryParameters;
    }

    public void setIncludeQueryParameters(boolean includeQueryParameters) {
        this.includeQueryParameters = includeQueryParameters;
    }

    public boolean isLogTakenConnectionsOnTimeout() {
        return logTakenConnectionsOnTimeout;
    }

    public void setLogTakenConnectionsOnTimeout(boolean logTakenConnectionsOnTimeout) {
        this.logTakenConnectionsOnTimeout = logTakenConnectionsOnTimeout;
    }

    public boolean isLogAllStackTracesOnTimeout() {
        return logAllStackTracesOnTimeout;
    }

    public void setLogAllStackTracesOnTimeout(boolean logAllStackTracesOnTimeout) {
        this.logAllStackTracesOnTimeout = logAllStackTracesOnTimeout;
    }

    public Pattern getLogLineRegex() {
        return logLineRegex;
    }

    public void setLogLineRegex(Pattern logLineRegex) {
        this.logLineRegex = logLineRegex;
    }

    public boolean isResetDefaultsAfterUse() {
        return resetDefaultsAfterUse;
    }

    public void setResetDefaultsAfterUse(boolean resetDefaultsAfterUse) {
        this.resetDefaultsAfterUse = resetDefaultsAfterUse;
    }

    public Boolean getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public Boolean getDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public String getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(String defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public Integer getDefaultTransactionIsolationIntValue() {
        return defaultTransactionIsolationIntValue;
    }

    public void setDefaultTransactionIsolationIntValue(Integer defaultTransactionIsolationIntValue) {
        this.defaultTransactionIsolationIntValue = defaultTransactionIsolationIntValue;
    }

    public boolean isClearSQLWarnings() {
        return clearSQLWarnings;
    }

    public void setClearSQLWarnings(boolean clearSQLWarnings) {
        this.clearSQLWarnings = clearSQLWarnings;
    }

    public ConnHooks getConnHooks() {
        return connHooks;
    }

    public InvocationHooks getInvocationHooks() {
        return invocationHooks;
    }

    @Override
    public String toString() {
        return super.toString() +
                "[driverClassName = " + driverClassName +
                ", jdbcUrl = " + jdbcUrl +
                ", username = " + username +
                ", externalDataSource = " + externalDataSource +
                ", poolInitialSize = " + poolInitialSize +
                ", poolMaxSize = " + poolMaxSize +
                ", poolFair = " + poolFair +
                ", pool = " + pool +
                ", name = " + name +
                ", connectionTimeoutInMs = " + connectionTimeoutInMs +
                ", loginTimeoutInSeconds = " + loginTimeoutInSeconds +
                ", acquireRetryDelayInMs = " + acquireRetryDelayInMs +
                ", acquireRetryAttempts = " + acquireRetryAttempts +
                ", statementCacheMaxSize = " + statementCacheMaxSize +
                ']';
    }
}
