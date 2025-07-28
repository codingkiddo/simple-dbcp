package com.simple.dbcp.pool;

public final class HookHolder {
	
	private HookHolder() { }

    public static ConnHooks newConnHooks() {
        return new ConnHooksHolder();
    }

    public static InvocationHooks newInvocationHooks() {
        return new InvocationHooksHolder();
    }

    public interface ConnHooks {
        void addOnInit(Hook.InitConnection hook);
        void addOnGet(Hook.GetConnection hook);
        void addOnClose(Hook.CloseConnection hook);
        void addOnDestroy(Hook.DestroyConnection hook);
        void addOnTimeout(Hook.GetConnectionTimeout hook);
    }

    interface ConnHooksAccessor { // for internal use only
        Hook.InitConnection[] onInit();
        Hook.GetConnection[] onGet();
        Hook.CloseConnection[] onClose();
        Hook.DestroyConnection[] onDestroy();
        Hook.GetConnectionTimeout[] onTimeout();
    }

    public interface InvocationHooks {
        void addOnMethodInvocation(Hook.MethodInvocation hook);
        void addOnStatementExecution(Hook.StatementExecution hook);
        void addOnResultSetRetrieval(Hook.ResultSetRetrieval hook);
    }

    public interface InvocationHooksAccessor { // for internal use only
        Hook.MethodInvocation[] onMethodInvocation();
        Hook.StatementExecution[] onStatementExecution();
        Hook.ResultSetRetrieval[] onResultSetRetrieval();
    }

    private static final class ConnHooksHolder implements ConnHooks, ConnHooksAccessor {
        private Hook.InitConnection[] onInit = {};
        private Hook.GetConnection[] onGet = {};
        private Hook.CloseConnection[] onClose = {};
        private Hook.DestroyConnection[] onDestroy = {};
        private Hook.GetConnectionTimeout[] onTimeout = {};

        @Override
        public void addOnInit(Hook.InitConnection hook) {
            onInit = addHook(onInit, hook);
        }

        @Override
        public void addOnGet(Hook.GetConnection hook) {
            onGet = addHook(onGet, hook);
        }

        @Override
        public void addOnClose(Hook.CloseConnection hook) {
            onClose = addHook(onClose, hook);
        }

        @Override
        public void addOnDestroy(Hook.DestroyConnection hook) {
            onDestroy = addHook(onDestroy, hook);
        }

        @Override
        public void addOnTimeout(Hook.GetConnectionTimeout hook) {
            onTimeout = addHook(onTimeout, hook);
        }

        @Override
        public Hook.InitConnection[] onInit() {
            return onInit;
        }

        @Override
        public Hook.GetConnection[] onGet() {
            return onGet;
        }

        @Override
        public Hook.CloseConnection[] onClose() {
            return onClose;
        }

        @Override
        public Hook.DestroyConnection[] onDestroy() {
            return onDestroy;
        }

        @Override
        public Hook.GetConnectionTimeout[] onTimeout() {
            return onTimeout;
        }
    }

    static final class InvocationHooksHolder implements InvocationHooks, InvocationHooksAccessor {

        private Hook.MethodInvocation[] onMethodInvocation = {};
        private Hook.StatementExecution[] onStatementExecution = {};
        private Hook.ResultSetRetrieval[] onResultSetRetrieval = {};

        @Override
        public void addOnMethodInvocation(Hook.MethodInvocation hook) {
            onMethodInvocation = addHook(onMethodInvocation, hook);
        }

        @Override
        public void addOnStatementExecution(Hook.StatementExecution hook) {
            onStatementExecution = addHook(onStatementExecution, hook);
        }

        @Override
        public void addOnResultSetRetrieval(Hook.ResultSetRetrieval hook) {
            onResultSetRetrieval = addHook(onResultSetRetrieval, hook);
        }

        @Override
        public Hook.MethodInvocation[] onMethodInvocation() {
            return onMethodInvocation;
        }

        @Override
        public Hook.StatementExecution[] onStatementExecution() {
            return onStatementExecution;
        }

        @Override
        public Hook.ResultSetRetrieval[] onResultSetRetrieval() {
            return onResultSetRetrieval;
        }
    }

}
