package com.simple.dbcp.pool;

import com.simple.dbcp.SimpleConfig;
import com.simple.dbcp.objectpool.util.TakenListener;

public class SimpleListener extends TakenListener<ConnHolder> {

	public static final TakenConnection[] NO_TAKEN_CONNECTIONS = {};
    private static final ConnHolder[] NO_TAKEN_CONN_HOLDERS = {};

    private final SimpleConfig config;

    public SimpleListener(SimpleConfig config) {
        super(config.getPoolMaxSize());
        this.config = config;
    }
}
