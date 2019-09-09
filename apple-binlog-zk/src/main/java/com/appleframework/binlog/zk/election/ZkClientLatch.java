package com.appleframework.binlog.zk.election;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;

public class ZkClientLatch {

	private ZkClientCache cache;
	private LeaderLatch leader;

	private CuratorFramework client;

	public ZkClientLatch(LeaderLatch leader, CuratorFramework client) {
		this.client = client;
		this.leader = leader;
	}

	/**
	 * 启动客户端
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void startZKClient() throws Exception {
		if (!client.isStarted()) {
			client.start();
		}
		leader.start();
	}

	/**
	 * 关闭客户端
	 * 
	 * @throws Exception
	 */
	public void closeZKClient() throws Exception {
		leader.close();
		client.close();
	}

	/**
	 * 判断是否变为领导者
	 * 
	 * @return
	 */
	public boolean hasLeadership() {
		return leader.hasLeadership() && cache.getIsLeader();
	}

	public LeaderLatch getLeader() {
		return leader;
	}

	public void setLeader(LeaderLatch leader) {
		this.leader = leader;
	}

	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public ZkClientCache getCache() {
		return cache;
	}

	public void setCache(ZkClientCache cache) {
		this.cache = cache;
	}

}
