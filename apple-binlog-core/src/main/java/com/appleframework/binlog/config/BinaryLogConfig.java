package com.appleframework.binlog.config;

public class BinaryLogConfig {

	private static String host;
	private static Integer port;
	private static String username;
	private static String password;
	private static Long serverId;

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		BinaryLogConfig.host = host;
	}

	public static Integer getPort() {
		return port;
	}

	public static void setPort(Integer port) {
		BinaryLogConfig.port = port;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		BinaryLogConfig.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		BinaryLogConfig.password = password;
	}

	public static Long getServerId() {
		return serverId;
	}

	public static void setServerId(Long serverId) {
		BinaryLogConfig.serverId = serverId;
	}

}