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
        
    private void threadStart() {
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
				}
            }
        });
		thread.setName("zk-application-booter");
		thread.setDaemon(true);
		thread.start();
    }
    
    private void threadStop() {
    	logger.debug("当前服务不是Leader, 停止所有");
    	if(applicationRunner.isRun()) {
    		applicationRunner.destory();
    	}
    	if(null != thread) {
    		thread = null;
    	}
    }
    
    private boolean isThreadExist() {
    	return (null == thread) ? false : true;
    }

	@Override
	public void run() {
		ZkClientLatch zkClient;
		try {
			zkClient = ZkClientUtil.getZkClient(ZkConfig.getZkClientInfo());
			logger.info("zk客户端连接成功");
			Thread.sleep(100);
			while (true) {
				if (!zkClient.hasLeadership()) {
					logger.debug("当前服务不是Leader");
					threadStop();
				} else {
					logger.debug("当前服务是Leader");
					if(!isThreadExist()) {
						logger.debug("当前服务是Leader，线程不存在，重新启动");
						this.threadStart();
					}
					else {
						if(thread.isAlive()) {
							if(applicationRunner.isRun()) {
								if (!applicationRunner.isConnected()) {
									applicationRunner.connect();
								}
							}
						} else {
							threadStop();
							threadStart();
						}
					}
				}
				Thread.sleep(ZkConfig.getZkClientInfo().getRetrySleepTime());
			}
		} catch (Exception e) {
			logger.error("启动异常，程序退出！", e);
		}
	}

	@Override
	public void destory() {
		threadStop();
	}
	
	@Override
	public boolean isRun() {
		return applicationRunner.isRun();
	}

}
