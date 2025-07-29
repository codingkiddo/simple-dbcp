package com.simple.dbcp.objectpool.util;

public interface ThreadedPoolReducer {

    void start();
    Thread.State getState();
    void terminate();
    
}
