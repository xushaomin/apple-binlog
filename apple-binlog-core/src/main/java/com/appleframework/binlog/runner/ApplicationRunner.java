package com.appleframework.binlog.runner;

public interface ApplicationRunner {

	public void run();

	public boolean isConnected();

	public void disconnect();

	public void connect();

	public void destory();

}
