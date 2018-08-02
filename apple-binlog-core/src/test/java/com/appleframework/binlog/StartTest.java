package com.appleframework.binlog;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.runner.BinLogApplicationRunner;;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/*.xml"})
public class StartTest {
	
	@Resource
	private BinLogApplicationRunner binLogApplicationRunner;
	@Test
	public void testAddOpinion1() {
		try {
			
			BinaryLogConfig.setHost("192.168.1.211");
			BinaryLogConfig.setPort(3306);
			BinaryLogConfig.setUsername("root");
			BinaryLogConfig.setPassword("bykj@2017~");
			BinaryLogConfig.setServerId(123456L);

			binLogApplicationRunner.run();
			System.in.read();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	


}