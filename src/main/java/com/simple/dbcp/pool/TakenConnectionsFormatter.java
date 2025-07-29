package com.simple.dbcp.pool;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.simple.dbcp.SimpleConfig;
import com.simple.dbcp.util.SimpleUtils;

public interface TakenConnectionsFormatter {

	String formatTakenConnections(TakenConnection[] takenConns);

	class Default implements TakenConnectionsFormatter {

		private final SimpleConfig config;

		public Default(SimpleConfig config) {
			this.config = config;
		}

		@Override
		public String formatTakenConnections(TakenConnection[] takenConns) {
			if (takenConns == null || takenConns.length == 0) {
				return "NO TAKEN CONNECTIONS\n";
			}

			// sort the thread holding connection for the longest time on top
			Arrays.sort(takenConns, Comparator.comparingLong(TakenConnection::getTakenNanoTime));

			var currentNanoTime = System.nanoTime();
			var builder = new StringBuilder(takenConns.length * 8192);
			var currentStackTraces = getCurrentStackTraces(takenConns);
			for (var i = 0; i < takenConns.length; i++) {
				var holdingThread = takenConns[i].getThread();
				builder.append("\n============\n(").append(i + 1).append('/').append(takenConns.length).append("), ")
						.append(takenConns[i].getProxyConnection()).append(", held for ")
						.append(TimeUnit.NANOSECONDS.toMillis(currentNanoTime - takenConns[i].getTakenNanoTime()));

				if (takenConns[i].getLastAccessNanoTime() == 0) {
					builder.append(" ms, has not been accessed");
				} else {
					builder.append(" ms, last accessed before ").append(
							TimeUnit.NANOSECONDS.toMillis(currentNanoTime - takenConns[i].getLastAccessNanoTime()))
							.append(" ms");
				}

				builder.append(", taken by thread ").append(holdingThread.getName()).append(", current thread state ")
						.append(holdingThread.getState())
						.append("\n\nThread stack trace at the moment when getting the Connection:\n")
						.append(SimpleUtils.getStackTraceAsString(config.getLogLineRegex(),
								takenConns[i].getLocation().getStackTrace()));

				var currentStackTrace = currentStackTraces.remove(holdingThread);
				if (currentStackTrace != null && currentStackTrace.length > 0) {
					builder.append("\nThread stack trace at the current moment:\n")
							.append(SimpleUtils.getStackTraceAsString(config.getLogLineRegex(), currentStackTrace));
				}
			}

			return addAllOtherStackTraces(builder, currentStackTraces).toString();
		}

		private StringBuilder addAllOtherStackTraces(StringBuilder builder,
				Map<Thread, StackTraceElement[]> stackTraces) {
			if (stackTraces.isEmpty()) {
				return builder;
			}

			builder.append("\n\n============ All other stack traces: ============\n\n");
			for (var entry : stackTraces.entrySet()) {
				var thread = entry.getKey();
				builder.append("\n============\n").append("Thread ").append(thread.getName()).append(", state ")
						.append(thread.getState());
				var currentStackTrace = entry.getValue();
				if (currentStackTrace.length > 0) {
					builder.append("\n\nThread stack trace at the current moment:\n")
							.append(SimpleUtils.getStackTraceAsString(config.getLogLineRegex(), currentStackTrace));
				}
			}
			return builder;
		}

		private Map<Thread, StackTraceElement[]> getCurrentStackTraces(TakenConnection[] takenConns) {
			if (config.isLogAllStackTracesOnTimeout()) {
				return Thread.getAllStackTraces();
			}

			Map<Thread, StackTraceElement[]> map = new HashMap<>(takenConns.length);
			for (var takenConn : takenConns) {
				var holdingThread = takenConn.getThread();
				if (holdingThread.isAlive()) {
					map.put(holdingThread, holdingThread.getStackTrace());
				}
			}
			return map;
		}
	}

}
