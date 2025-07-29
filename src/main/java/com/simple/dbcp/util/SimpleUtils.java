package com.simple.dbcp.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.simple.dbcp.SimpleConfig;
import com.simple.dbcp.objectpool.BasePool;

public class SimpleUtils {

	private SimpleUtils() {
	}

	public static String getPoolName(SimpleConfig config) {
		BasePool pool = config.getPool();
		var initialState = pool.isTerminated();
		var result = config.getName() + '@' + Integer.toHexString(config.hashCode()) + '(' + pool.taken() + '/'
				+ pool.remainingCreated() + '/' + pool.maxSize() + '/' + (!initialState ? 'w' : 't') // poolState: w ==
																										// working, t ==
																										// terminated
				+ '/' + (Thread.currentThread().isInterrupted() ? 'i' : 'n') + ')';
		if (initialState == pool.isTerminated()) { // make sure the pool state has not changed in the meantime
			return result;
		}
		return getPoolName(config); // this is one level of recursion only, pool state changes only once
	}

	public static String getStackTraceAsString(Pattern logLinePattern, StackTraceElement[] stackTrace) {
		if (stackTrace == null || stackTrace.length == 0) {
			return "EMPTY STACK TRACE\n";
		}

		int i;
		for (i = 0; i < stackTrace.length; i++) {
			if (!stackTrace[i].getClassName().startsWith("org.vibur")
					|| stackTrace[i].getMethodName().equals("getConnection")) {
				break;
			}
		}

		var builder = new StringBuilder(4096);
		for (i++; i < stackTrace.length; i++) {
			var stackTraceStr = stackTrace[i].toString();
			if (logLinePattern == null || logLinePattern.matcher(stackTraceStr).matches()) {
				builder.append("  at ").append(stackTraceStr).append('\n');
			}
		}
		return builder.toString();
	}

	public static String formatSql(String sqlQuery, List<Object[]> sqlQueryParams) {
		var result = new StringBuilder(1024).append("-- ").append(sqlQuery);

		if (sqlQueryParams != null && !sqlQueryParams.isEmpty()) {
			var params = sqlQueryParams.toArray();
			Arrays.sort(params, Comparator.comparing(obj -> ((Object[]) obj)[1].toString()));

			result.append("\n-- Parameters:\n-- ").append(Arrays.deepToString(params));
		}
		return result.toString();
	}

}
