package com.appleframework.binlog.runner;

import java.io.IOException;
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
		if (BinaryLogConfig.isRun()) {
			throw new IllegalStateException("binlog监控正在运行...");
		}
		if (BinaryLogConfig.isBinLogInit()) {
			initBinaryLogStatus();
		}

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
				logger.info("mysql binlog server connect success");
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

		try {
			// 启动连接
			BinaryLogConfig.setRun(true);
			client.connect();
		} catch (Exception e) {
			logger.error("连接异常", e);
		} finally {
			BinaryLogConfig.setRun(false);
			disconnect();
		}

	}
	
	@Override
	public boolean isRun() {
        return BinaryLogConfig.isRun();
    }

    public boolean isConnected() {
        return null != client && client.isConnected();
    }

    public void disconnect() {
        try {
            if (null != client) {
                client.disconnect();
            }
        } catch (IOException e) {
            logger.error("断开连接异常", e);
        }
    }

    public void connect() {
        try {
            if (null != client) {
                client.connect();
            }
        } catch (IOException e) {
            logger.error("重连失败", e);
        }
    }

	private void initBinaryLogStatus() {
		logger.warn("init binary log status,fileName={},position={}", 
				BinaryLogConfig.getBinlogFilename(), BinaryLogConfig.getBinlogPosition());
		if (null != BinaryLogConfig.getBinlogFilename() && null != BinaryLogConfig.getBinlogPosition()) {
			logStatusSync.updateBinaryLogStatus(BinaryLogConfig.getServerId(), 
					BinaryLogConfig.getBinlogFilename(), BinaryLogConfig.getBinlogPosition());
		} else {
			logStatusSync.initBinaryLogStatus(BinaryLogConfig.getServerId());
		}
	}

    /**
     * 配置当前binlog位置
     * 
     * @param client
     */
	private void configBinaryLogStatus(BinaryLogClient client) {
		logger.warn("config binary log status ...");
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
			logger.warn("config binary log status, fileName={},position={}", binlogFilename, binlogPosition);
		} else {
			logger.warn("config binary log status is null ");
		}
	}

    public void destory() {
        if (null != client) {
            try {
                client.disconnect();
                BinaryLogConfig.setRun(false);
            } catch (IOException e) {
            }
        }
    }

}