package com.appleframework.binlog.zk;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.runner.ApplicationRunner;
import com.appleframework.binlog.zk.booter.ZkApplicationBooter;
import com.appleframework.binlog.zk.config.ZkConfig;
import com.appleframework.binlog.zk.election.ZkClientInfo;;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/*.xml"})
public class StartTest {
	
	@Resource
	private ApplicationRunner applicationRunner;
	
	private static ZkClientInfo zkClientInfo;
	
	static {
		BinaryLogConfig.setBinLogInit(true);
		BinaryLogConfig.setBinlogFilename("mysql-bin.000729");
		BinaryLogConfig.setBinlogPosition(115119064L);
		BinaryLogConfig.setHost("192.168.1.211");
		BinaryLogConfig.setPort(3306);
		BinaryLogConfig.setUsername("rd");
		BinaryLogConfig.setPassword("azD6t5l638xTWdMhV3hK1XzUu");
		BinaryLogConfig.setServerId(123456L);
		
		zkClientInfo = new ZkClientInfo();
		zkClientInfo.setConnectString("192.168.1.217:4181,192.168.1.218:4181,192.168.1.219:4181");
		zkClientInfo.setId("2");
		zkClientInfo.setLeaderPath("/test/binlog/master");
		zkClientInfo.setDataPath("/test/binlog/data");
		ZkConfig.setZkClientInfo(zkClientInfo);
	}
	
	@Test
	public void testAddOpinion1() {
		try {
			
			ZkApplicationBooter booter = new ZkApplicationBooter();
			booter.setApplicationRunner(applicationRunner);
			booter.run();
			
			System.in.read();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	


}