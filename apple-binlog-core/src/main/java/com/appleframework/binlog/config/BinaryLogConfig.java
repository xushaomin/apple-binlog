package com.appleframework.binlog.config;

public class BinaryLogConfig {

	private static String host;
	private static Integer port;
	private static String username;
	private static String password;
	private static Long serverId = 2L;
	
	private static boolean run = false;
	
	private static boolean binLogInit = false;
	private static String binlogFilename = null;
	private static Long binlogPosition = null;

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

	public static boolean isRun() {
		return run;
	}

	public static void setRun(boolean run) {
		BinaryLogConfig.run = run;
	}

	public static boolean isBinLogInit() {
		return binLogInit;
	}

	public static void setBinLogInit(boolean binLogInit) {
		BinaryLogConfig.binLogInit = binLogInit;
	}

	public static String getBinlogFilename() {
		return binlogFilename;
	}

	public static void setBinlogFilename(String binlogFilename) {
		BinaryLogConfig.binlogFilename = binlogFilename;
	}

	public static Long getBinlogPosition() {
		return binlogPosition;
	}

	public static void setBinlogPosition(Long binlogPosition) {
		BinaryLogConfig.binlogPosition = binlogPosition;
	}
	
}