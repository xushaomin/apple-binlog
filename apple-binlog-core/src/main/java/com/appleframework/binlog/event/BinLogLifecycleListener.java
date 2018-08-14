package com.appleframework.binlog.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.LifecycleListener;

@Component
public class BinLogLifecycleListener implements LifecycleListener {

	private static final Logger log = LoggerFactory.getLogger(BinLogLifecycleListener.class);

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
}
