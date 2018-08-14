package com.appleframework.binlog.zk.election;

import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKClientListener implements LeaderLatchListener {

	private static Logger logger = LoggerFactory.getLogger(ZKClientListener.class);
	
	private ZkClientCache cache;
	
	@Override
	public void isLeader() {
		logger.info("当前服务已变为leader,将从事消费业务");
		cache.setIsLeader(true);
	}

	@Override
	public void notLeader() {
		logger.info("当前服务已退出leader,不再从事消费业务");
		cache.setIsLeader(false);
	}

	public ZkClientCache getCache() {
		return cache;
	}

	public void setCache(ZkClientCache cache) {
		this.cache = cache;
	}

}