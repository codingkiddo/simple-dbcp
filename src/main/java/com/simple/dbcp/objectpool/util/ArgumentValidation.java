package com.simple.dbcp.objectpool.util;

public class ArgumentValidation {

	private ArgumentValidation() {
	}

	public static void forbidIllegalArgument(boolean condition) {
		if (condition) {
			throw new IllegalArgumentException();
		}
	}

	public static void forbidIllegalArgument(boolean condition, String param) {
		if (condition) {
			throw new IllegalArgumentException("Illegal value for parameter: \"" + param + "\"");
		}
	}
}
