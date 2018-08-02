package com.appleframework.binlog.runner;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.service.BinLogEventHandler;
import com.appleframework.binlog.service.BinLogEventHandlerFactory;
import com.appleframework.binlog.status.LogStatusSync;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.EventHeader;

public class BinLogApplicationRunner {
    
	private static final Logger log = LoggerFactory.getLogger(BinLogApplicationRunner.class);

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

	public void run() {
        // 在线程中启动事件监听
        executorService.submit(() -> {
        	String username = BinaryLogConfig.username;
        	int port = BinaryLogConfig.port;
        	String host = BinaryLogConfig.host;
        	String password = BinaryLogConfig.password;
            final BinaryLogClient client = new BinaryLogClient(host, port, username, password);
            client.registerEventListener(event -> {
                EventHeader header = event.getHeader();
                BinLogEventHandler handler = binLogEventHandlerFactory.getHandler(header);
                handler.handle(event);
            });
            client.registerLifecycleListener(new BinaryLogClient.LifecycleListener() {

                @Override
                public void onConnect(BinaryLogClient client) {
                    log.info("connect success");
                }

                @Override
                public void onCommunicationFailure(BinaryLogClient client, Exception ex) {
                    log.error("communication fail", ex);
                }

                @Override
                public void onEventDeserializationFailure(BinaryLogClient client, Exception ex) {
                    log.error("event deserialization fail", ex);
                }

                @Override
                public void onDisconnect(BinaryLogClient client) {
                    log.warn("disconnect");
                }
            });
            // 设置server id
            client.setServerId(BinaryLogConfig.serverId);
            // 配置当前位置
            configBinaryLogStatus(client);
            // 启动连接
            try {
                client.connect();
            } catch (Exception e) {
                // TODO: 17/01/2018 继续优化异常处理逻辑
                log.error("处理事件异常，{}", e);
            }
        });
    }

    /**
     * 配置当前binlog位置
     * @param client
     */
    private void configBinaryLogStatus(BinaryLogClient client) {
        Map<String, Object> binLogStatus = logStatusSync.getBinaryLogStatus(client.getServerId());
        if (binLogStatus != null) {
            Object binlogFilename = binLogStatus.get("binlogFilename");
            if (binlogFilename != null) {
                client.setBinlogFilename((String) binlogFilename);
            }
            Object binlogPosition = binLogStatus.get("binlogPosition");
            if (binlogPosition != null) {
                client.setBinlogPosition((Long) binlogPosition);
            }
        }
    }

}
