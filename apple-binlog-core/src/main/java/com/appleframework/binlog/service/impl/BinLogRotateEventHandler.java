package com.appleframework.binlog.service.impl;

import org.springframework.stereotype.Service;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.service.BinLogEventHandler;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.RotateEventData;

/**
 * RotateEvent，主要是更新文件位置
 */
@Service
public class BinLogRotateEventHandler extends BinLogEventHandler {

    @Override
    public void handle(Event event) {
        RotateEventData d = event.getData();
        String binlogFilename = d.getBinlogFilename();
        Long binlogPosition = d.getBinlogPosition();
        logStatusSync.updateBinaryLogStatus(BinaryLogConfig.getServerId(), binlogFilename, binlogPosition);
    }

	@Override
	protected EventBaseDTO formatData(Event event) {
		return null;
	}
    
}
