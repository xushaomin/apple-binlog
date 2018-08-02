package com.appleframework.binlog.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.shyiko.mysql.binlog.event.Event;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.service.BinLogEventHandler;

@Service
public class BinLogDefaultEventHandler extends BinLogEventHandler {

    private static final Logger log = LoggerFactory.getLogger(BinLogDefaultEventHandler.class);

    @Override
    public void handle(Event event) {
        log.error("跳过不处理事件event:{}", event);
    }

	@Override
	protected EventBaseDTO formatData(Event event) {
		return null;
	}    
}
