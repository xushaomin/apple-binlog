package com.appleframework.binlog.zk.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.runner.ApplicationRunner;
import com.appleframework.binlog.zk.config.ZkConfig;
import com.appleframework.binlog.zk.election.ZkClient;
import com.appleframework.binlog.zk.election.ZkClientUtil;

public class ZkApplicationRunner implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(ZkApplicationRunner.class);

	private ApplicationRunner applicationRunner;

	public void setApplicationRunner(ApplicationRunner applicationRunner) {
		this.applicationRunner = applicationRunner;
	}

	@Override
	public void run() {
		ZkClient zkClient;
		try {
			zkClient = ZkClientUtil.getZkClient(ZkConfig.getZkClientInfo());
			logger.info("zk客户端连接成功");
			Thread.sleep(100);
			while (true) {
				// 第一步leader验证
				if (!zkClient.hasLeadership()) {
					logger.info("当前服务不是Leader");
					if (applicationRunner.isConnected()) {
						applicationRunner.disconnect();
					}
				} else {
					logger.info("当前服务是Leader");
					if (BinaryLogConfig.isRun()) {
						if (!applicationRunner.isConnected()) {
							applicationRunner.connect();
						}
					} else {
						applicationRunner.run();
					}
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public boolean isConnected() {
		return applicationRunner.isConnected();
	}

	@Override
	public void disconnect() {
		applicationRunner.disconnect();
	}

	@Override
	public void connect() {
		applicationRunner.connect();
	}

	@Override
	public void destory() {
		applicationRunner.destory();
	}

}
