package com.appleframework.binlog.zk.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appleframework.binlog.zk.election.ZkClientLatch;
import com.appleframework.binlog.zk.election.ZkClientInfo;
import com.appleframework.binlog.zk.election.ZkClientUtil;

public class ZkClientTest {

	private static Log log = LogFactory.getLog(ZkClientTest.class);

	public static void main(String[] args) throws Exception {
		ZkClientInfo zkClientInfo = new ZkClientInfo();
		zkClientInfo.setId("1");
		zkClientInfo.setLeaderPath("/apple/binlog/master");
		zkClientInfo.setDataPath("/apple/binlog/data");
		ZkClientLatch zkClient = ZkClientUtil.getZkClient(zkClientInfo);
		
		log.info("zk客户端连接成功");

		while(true) {
			//第一步leader验证 
			if (!zkClient.hasLeadership()) {
				log.info("当前服务不是leader");
				Thread.sleep(3000);
				continue;
			} else {
				log.info("当前服务是leader");
			}
			Thread.sleep(1000);
			log.info("Test01 do it... ");
		}

	}
}
