package com.appleframework.binlog.zk;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.runner.BinLogApplicationRunner;
import com.appleframework.binlog.zk.config.ZkConfig;
import com.appleframework.binlog.zk.election.ZkClient;
import com.appleframework.binlog.zk.election.ZkClientInfo;
import com.appleframework.binlog.zk.election.ZkClientUtil;;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/*.xml"})
public class StartTest2 {
	
	@Resource
	private BinLogApplicationRunner binLogApplicationRunner;
	
	private static ZkClientInfo zkClientInfo;
	
	static {
		BinaryLogConfig.setHost("192.168.1.211");
		BinaryLogConfig.setPort(3306);
		BinaryLogConfig.setUsername("root");
		BinaryLogConfig.setPassword("bykj@2017~");
		BinaryLogConfig.setServerId(123456L);
		
		zkClientInfo = new ZkClientInfo();
		zkClientInfo.setId("2");
		zkClientInfo.setLeaderPath("/apple/binlog/master");
		zkClientInfo.setDataPath("/apple/binlog/data");
		ZkConfig.setZkClientInfo(zkClientInfo);
	}
	
	@Test
	public void testAddOpinion1() {
		try {
			
			
			ZkClient zkClient = ZkClientUtil.getZkClient(zkClientInfo);
			
			ZkConfig.setZkClientInfo(zkClientInfo);
			System.out.println("zk客户端连接成功");

			Thread.sleep(3000);
			while(true) {
				
				//第一步leader验证 
				if (!zkClient.hasLeadership()) {
					System.out.println("当前服务不是leader");
					if(binLogApplicationRunner.isConnected()) {
						binLogApplicationRunner.disconnect();
					}
				} else {
					System.out.println("当前服务是leader");
					if(BinaryLogConfig.isRun()) {
						if(!binLogApplicationRunner.isConnected()) {
							binLogApplicationRunner.connect();
						}
					}
					else {
						binLogApplicationRunner.run();
					}
				}
				System.out.println("Test02 do it... ");
				Thread.sleep(5000);
			}
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	


}