package com.appleframework.binlog.zk.election;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;

public class ZkClientSelector {

	private LeaderSelector leader;

	private CuratorFramework client;

	public ZkClientSelector(LeaderSelector leader, CuratorFramework client) {
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
		return leader.hasLeadership();
	}

	public LeaderSelector getLeader() {
		return leader;
	}

	public void setLeader(LeaderSelector leader) {
		this.leader = leader;
	}

	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

}
