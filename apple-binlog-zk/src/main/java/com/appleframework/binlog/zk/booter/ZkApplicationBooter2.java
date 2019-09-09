package com.appleframework.binlog.zk.booter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.binlog.booter.ApplicationBooter;
import com.appleframework.binlog.runner.ApplicationRunner;
import com.appleframework.binlog.zk.config.ZkConfig;
import com.appleframework.binlog.zk.election.ZkClientSelector;
import com.appleframework.binlog.zk.election.ZkClientUtil;

public class ZkApplicationBooter2 implements ApplicationBooter {

	private static final Logger logger = LoggerFactory.getLogger(ZkApplicationBooter2.class);

	private boolean isDestory = false;

	private ApplicationRunner applicationRunner;

	public void setApplicationRunner(ApplicationRunner applicationRunner) {
		this.applicationRunner = applicationRunner;
	}

	private ZkClientSelector zkClient;
	
	private Thread thread;

	/**
	 * 是否有领导权
	 */
	private boolean hasLeadership() {
		if (zkClient != null) {
			return zkClient.hasLeadership();
		}
		return false;
	}

	/**
	 * 放弃领导权
	 */
	private void relinquished() {
		logger.warn("主动放弃领导权...");
		applicationRunner.disconnect();
	}
	
	/**
	 * 启动线程，判断程序是否健康，不健康退出选举
	 */
	private void requeue() {
		if(null == thread) {
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					if (zkClient.getLeader() != null) {
						// 为了让出领导权，让其他节点有足够的时间获取领导权
						int sheepTime = ZkConfig.getZkClientInfo().getRetrySleepTime();
						try {
							Thread.sleep(sheepTime);
						} catch (InterruptedException e) {
							logger.error(e.getMessage());
						}
						// 如果程序已经在停止了，就不参与竞选了
						if (!isDestory) {
							logger.info("休眠{}秒之后节点再次开始竞选Leader...", sheepTime);
							zkClient.getLeader().requeue();
						}
					}
				}
			});
			thread.setName("zk-application-booter");
			thread.setDaemon(true);
			thread.start();
		}
	}

	@Override
	public void run() {
		logger.warn("开始竞选Leader...");
		try {
			LeaderSelectorListener listener = new LeaderSelectorListenerAdapter() {
				
				@Override
				public void takeLeadership(CuratorFramework client) throws Exception {
					logger.warn("当前节点成功竞选为Leader，开始启动BinLog监听...");
					try {
						applicationRunner.run();
					} catch (Exception e) {
						logger.error("BinLog监听异常", e);
					} finally {
						// 放弃领导权，并断开
						relinquished();
					}

					requeue();
				}

				@Override
				public void stateChanged(CuratorFramework client, ConnectionState newState) {
					logger.warn("ZK连接状态改变：{}", newState);
					if (client.getConnectionStateErrorPolicy().isErrorState(newState)) {
						if (hasLeadership()) {
							logger.error("连接丢失，放弃Leader");
							relinquished();
						}
					}
				}
				
			};
			zkClient = ZkClientUtil.getZkClient(ZkConfig.getZkClientInfo(), listener);
		} catch (Exception e) {
			logger.error("启动异常！", e);
			destory();
		}
	}

	@Override
	public void destory() {
		isDestory = true;
		applicationRunner.destory();
	}

	@Override
	public boolean isRun() {
		return applicationRunner.isRun();
	}

}