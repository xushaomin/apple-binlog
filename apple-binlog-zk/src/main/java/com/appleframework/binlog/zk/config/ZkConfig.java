package com.appleframework.binlog.zk.config;

import com.appleframework.binlog.zk.election.ZkClientInfo;

public class ZkConfig {

	private static ZkClientInfo ZkClientInfo;

	public static ZkClientInfo getZkClientInfo() {
		return ZkClientInfo;
	}

	public static void setZkClientInfo(ZkClientInfo zkClientInfo) {
		ZkClientInfo = zkClientInfo;
	}

}
