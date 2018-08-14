package com.appleframework.binlog.event;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.appleframework.binlog.service.BinLogEventHandler;
import com.appleframework.binlog.service.BinLogEventHandlerFactory;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeader;

@Component
public class BinLogEventLister implements EventListener {

	@Resource
    private BinLogEventHandlerFactory binLogEventHandlerFactory;
	
	@Override
	public void onEvent(Event event) {
		EventHeader header = event.getHeader();
        BinLogEventHandler handler = binLogEventHandlerFactory.getHandler(header);
        handler.handle(event);
	}

	public void setBinLogEventHandlerFactory(BinLogEventHandlerFactory binLogEventHandlerFactory) {
		this.binLogEventHandlerFactory = binLogEventHandlerFactory;
	}
	
}
