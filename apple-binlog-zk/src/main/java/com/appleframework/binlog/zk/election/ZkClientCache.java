package com.appleframework.binlog.zk.election;

public class ZkClientCache {

	private boolean isLeader = false;

	public Boolean getIsLeader() {
		return isLeader;
	}

	public void setIsLeader(Boolean isLeader) {
		this.isLeader = isLeader;
	}

}
