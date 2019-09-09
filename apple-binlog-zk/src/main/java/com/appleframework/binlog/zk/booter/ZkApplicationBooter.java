package com.appleframework.binlog.zk.booter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.binlog.booter.ApplicationBooter;
import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.runner.ApplicationRunner;
import com.appleframework.binlog.zk.config.ZkConfig;
import com.appleframework.binlog.zk.election.ZkClientLatch;
import com.appleframework.binlog.zk.election.ZkClientUtil;

public class ZkApplicationBooter implements ApplicationBooter {

	private static final Logger logger = LoggerFactory.getLogger(ZkApplicationBooter.class);

	private ApplicationRunner applicationRunner;

	public void setApplicationRunner(ApplicationRunner applicationRunner) {
		this.applicationRunner = applicationRunner;
	}
	
    private boolean isRun = false;

	@Override
	public void run() {
		ZkClientLatch zkClient;
		try {
			zkClient = ZkClientUtil.getZkClient(ZkConfig.getZkClientInfo());
			logger.info("zk客户端连接成功");
			Thread.sleep(100);
			while (true) {
				// 第一步leader验证
				if (!zkClient.hasLeadership()) {
					logger.debug("当前服务不是Leader");
					if (BinaryLogConfig.isRun()) {
						applicationRunner.destory();
					}
				} else {
					logger.debug("当前服务是Leader");
					if (BinaryLogConfig.isRun()) {
						if (!applicationRunner.isConnected()) {
							applicationRunner.connect();
						}
					} else {
						if(isRun) {
							applicationRunner.connect();
						}
						else {
							Thread t = new Thread(new Runnable() {
		                        @Override
		                        public void run() {
		                        	isRun = true;
		                        	applicationRunner.run();
		                        }
		                    });
							t.setName("sync-binlog-booter");
		                    t.setDaemon(true);
		                    t.start();
						}
					}
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			logger.error("启动异常，程序退出！", e);
            System.exit(1);
		}
	}

	@Override
	public void destory() {
		applicationRunner.destory();
	}
	
	@Override
	public boolean isRun() {
		return applicationRunner.isRun();
	}

}
