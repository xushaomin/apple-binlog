package com.appleframework.binlog.zk.election;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ZkClientUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ZkClientUtil.class);
	
	private static ConcurrentHashMap<String, ZkClient> zkClientMap = new ConcurrentHashMap<String, ZkClient>();
	private static ConcurrentHashMap<String, CuratorFramework> cfClientMap = new ConcurrentHashMap<String, CuratorFramework>();
	
	public static ZkClient getZkClient(ZkClientInfo zkClientInfo) throws Exception {
		ZkClient zkClient = zkClientMap.get(zkClientInfo.getId());
		if(null == zkClient) {
			synchronized (ZkClient.class) {
				CuratorFramework cfClient = getCuratorClient(zkClientInfo);
				LeaderLatch leaderLatch = new LeaderLatch(cfClient, zkClientInfo.getLeaderPath(), zkClientInfo.getId(),
						LeaderLatch.CloseMode.NOTIFY_LEADER);
				ZkClientCache cache = new ZkClientCache();
				ZKClientListener zkClientListener = new ZKClientListener();
				zkClientListener.setCache(cache);
				leaderLatch.addListener(zkClientListener);
				zkClient = new ZkClient(leaderLatch, cfClient);
				zkClient.setCache(cache);
				zkClientMap.put(zkClientInfo.getId(), zkClient);
				zkClient.startZKClient();
			}
		}
		return zkClient;
	}
	
	public static CuratorFramework getCuratorClient(ZkClientInfo zkClientInfo) throws Exception {
		CuratorFramework cfClient = cfClientMap.get(zkClientInfo.getId());
		if(null == cfClient) {
			synchronized (CuratorFramework.class) {
				RetryPolicy retryPolicy = 
		        		new ExponentialBackoffRetry(zkClientInfo.getRetrySleepTime(), zkClientInfo.getMaxRetries());
				cfClient = CuratorFrameworkFactory.builder()
						.connectString(zkClientInfo.getConnectString())
						.retryPolicy(retryPolicy)
						.connectionTimeoutMs(zkClientInfo.getConnectTimeOut()).build();
				cfClient.start();
				cfClientMap.put(zkClientInfo.getId(), cfClient);
			}
		}
		return cfClient;
	}
	
	// 增加永久节点
	public static void createPersistent(ZkClientInfo zkClientInfo, String path, String value) {
		try {
			String dataPath = zkClientInfo.getDataPath() + "/" + path;
			CuratorFramework client = getCuratorClient(zkClientInfo);
			String[] paths = dataPath.split("/");
			String existPath = "";
			for (int i = 0; i < paths.length; i++) {
				if (StringUtils.isEmpty(paths[i])) {
					continue;
				}
				existPath = existPath + "/" + paths[i];
				if (!existN(zkClientInfo, existPath)) {
					client.create().withMode(CreateMode.PERSISTENT).forPath(existPath, value.getBytes());
					// 创建永久路径
					logger.debug("path not exist,create persistent path: " + path + " with value" + value + " succeed");
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
	
	public static void createPersistentN(ZkClientInfo zkClientInfo, String path, String value) {
		try {
			CuratorFramework client = getCuratorClient(zkClientInfo);
			client.create().withMode(CreateMode.PERSISTENT).forPath(path, value.getBytes());
			// 创建永久路径
			logger.debug("path not exist,create persistent path: " + path + " with value" + value + " succeed");
			// LoggerUtils.debug(logger,
			// "--------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
	}
	
	public static void update(ZkClientInfo zkClientInfo, String path, String value) {
		long begin = System.currentTimeMillis();
		try {
			String dataPath = zkClientInfo.getDataPath() + "/" + path;
			CuratorFramework client = getCuratorClient(zkClientInfo);
			client.setData().forPath(dataPath, value.getBytes());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		long end = System.currentTimeMillis();
		logger.debug("update zk data cost:" + (end - begin) + " ms " + path + ":" + value);
	}
	
	// 查
	public static String getData(ZkClientInfo zkClientInfo, String path) {
		try {
			String dataPath = zkClientInfo.getDataPath() + "/" + path;
			CuratorFramework client = getCuratorClient(zkClientInfo);
			byte[] data = client.getData().forPath(dataPath);
			return new String(data);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}
	
	// 是否存在
	public static boolean exist(ZkClientInfo zkClientInfo, String path) {
		boolean exist = false;
		try {
			String dataPath = zkClientInfo.getDataPath() + "/" + path;
			CuratorFramework client = getCuratorClient(zkClientInfo);
			if (null != client.checkExists().forPath(dataPath)) {
				exist = true;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return exist;
	}
	
	public static boolean existN(ZkClientInfo zkClientInfo, String path) {
		boolean exist = false;
		try {
			CuratorFramework client = getCuratorClient(zkClientInfo);
			if (null != client.checkExists().forPath(path)) {
				exist = true;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return exist;
	}
	
}
