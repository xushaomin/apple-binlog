package com.appleframework.binlog.zk.booter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.binlog.booter.ApplicationBooter;
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
    
    private Thread thread = null;
    
    private void threadRun() {
		thread = new Thread(new Runnable() {
            @Override
            public void run() {
            	try {
            		applicationRunner.run();
				} catch (Exception e) {
					logger.error("BinLog监听异常", e);
				} finally {
					logger.warn("主动放弃领导权...");
					applicationRunner.destory();
					thread = null;
				}
            }
        });
		thread.setName("zk-application-booter");
		thread.setDaemon(true);
		thread.start();
    }

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
					logger.info("当前服务不是Leader");
					if(null != thread && thread.isAlive() && applicationRunner.isRun()) {
						applicationRunner.destory();
						thread = null;
						logger.info("当前服务不是Leader, 线程被初始化为空");
					}
				} else {
					logger.info("当前服务是Leader");
					if(null == thread) {
						logger.info("当前服务是Leader，线程重新启动");
						this.threadRun();
					}
					else {
						if(thread.isAlive()) {
							if(applicationRunner.isRun()) {
								if (!applicationRunner.isConnected()) {
									applicationRunner.connect();
								}
							}
							else {
								applicationRunner.run();
							}
						}
						else {
							thread = null;
						}
					}
				}
				Thread.sleep(ZkConfig.getZkClientInfo().getRetrySleepTime());
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
