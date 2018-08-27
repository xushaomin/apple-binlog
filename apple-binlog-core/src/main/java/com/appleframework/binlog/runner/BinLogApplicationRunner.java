package com.appleframework.binlog.runner;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.model.LogStatus;
import com.appleframework.binlog.service.BinLogEventHandler;
import com.appleframework.binlog.service.BinLogEventHandlerFactory;
import com.appleframework.binlog.status.LogStatusSync;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.EventHeader;

@Component("applicationRunner")
public class BinLogApplicationRunner implements ApplicationRunner {
    
	private static final Logger logger = LoggerFactory.getLogger(BinLogApplicationRunner.class);

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Resource
	private LogStatusSync logStatusSync;

    @Resource
    private BinLogEventHandlerFactory binLogEventHandlerFactory;

    public void setLogStatusSync(LogStatusSync logStatusSync) {
		this.logStatusSync = logStatusSync;
	}

	public void setBinLogEventHandlerFactory(BinLogEventHandlerFactory binLogEventHandlerFactory) {
		this.binLogEventHandlerFactory = binLogEventHandlerFactory;
	}
	
	private BinaryLogClient client;

	public void run() {
		if(BinaryLogConfig.isBinLogInit()) {
			initBinaryLogStatus();
		}
        // 在线程中启动事件监听
        executorService.submit(() -> {
        	String username = BinaryLogConfig.getUsername();
        	int port = BinaryLogConfig.getPort();
        	String host = BinaryLogConfig.getHost();
        	String password = BinaryLogConfig.getPassword();
            client = new BinaryLogClient(host, port, username, password);
            client.registerEventListener(event -> {
                EventHeader header = event.getHeader();
                BinLogEventHandler handler = binLogEventHandlerFactory.getHandler(header);
                handler.handle(event);
            });
            client.registerLifecycleListener(new BinaryLogClient.LifecycleListener() {
                @Override
                public void onConnect(BinaryLogClient client) {
                    logger.info("connect success");
                }

                @Override
                public void onCommunicationFailure(BinaryLogClient client, Exception ex) {
                    logger.error("communication fail", ex);
                }

                @Override
                public void onEventDeserializationFailure(BinaryLogClient client, Exception ex) {
                    logger.error("event deserialization fail", ex);
                }

                @Override
                public void onDisconnect(BinaryLogClient client) {
                    logger.warn("disconnect");
                }
            });
            // 设置server id
            client.setServerId(BinaryLogConfig.getServerId());
            // 配置当前位置
            configBinaryLogStatus(client);
            // 启动连接
            try {
            	BinaryLogConfig.setRun(true);
                client.connect();
            } catch (Exception e) {
                logger.error("连接失败，{}", e.getMessage());
                client.setBinlogFilename(null);
                client.setBinlogPosition(0);
                initBinaryLogStatus();
                //resetBinaryLogStatus();
                try {
					client.disconnect();
	                client.connect();
				} catch (Exception e1) {
					logger.error("重连失败，{}", e1.getMessage());
				}
            }
        });
    }
	
	public boolean isConnected() {
		return null!= client && client.isConnected();
	}
	
	public void disconnect() {
		try {
			if(null != client) {
				client.disconnect();
			}
		} catch (IOException e) {
			logger.error("断开失败，{}", e.getMessage());
		}
	}
	
	public void connect() {
		try {
			if(null != client) {
				client.connect();
			}
		} catch (IOException e) {
			logger.error("重连失败，{}", e.getMessage());
		}
	}
	
	private void initBinaryLogStatus() {
		logger.info("init binary log status,fileName={},position={}", 
				BinaryLogConfig.getBinlogFilename(),BinaryLogConfig.getBinlogPosition());
		if(null != BinaryLogConfig.getBinlogFilename() && null != BinaryLogConfig.getBinlogPosition()) {
			logStatusSync.updateBinaryLogStatus(BinaryLogConfig.getServerId(), 
					BinaryLogConfig.getBinlogFilename(), BinaryLogConfig.getBinlogPosition());
		}
		else {
			logStatusSync.initBinaryLogStatus(BinaryLogConfig.getServerId());
		}
	}

	/**
	 * 配置当前binlog位置
	 * @param client
	 */
	private void configBinaryLogStatus(BinaryLogClient client) {
		logger.info("config binary log status ...");
		LogStatus binLogStatus = logStatusSync.getBinaryLogStatus(client.getServerId());
		if (binLogStatus != null) {
			String binlogFilename = binLogStatus.getBinlogFilename();
			if (binlogFilename != null && !binlogFilename.equalsIgnoreCase("null")) {
				client.setBinlogFilename(binlogFilename);
			}
			Long binlogPosition = binLogStatus.getBinlogPosition();
			if (binlogPosition != null) {
				client.setBinlogPosition(binlogPosition);
			}
			logger.info("config binary log status, fileName={},position={}", binlogFilename, binlogPosition);
		}
		else {
			logger.info("config binary log status is null ");
		}
	}

	public void destory() {
		if(null != client) {
			try {
				client.disconnect();
			} catch (IOException e) {
			}
		}
	}

}
