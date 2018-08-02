package com.appleframework.binlog.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.appleframework.binlog.service.impl.BinLogDefaultEventHandler;
import com.appleframework.binlog.service.impl.BinLogDeleteEventHandler;
import com.appleframework.binlog.service.impl.BinLogRotateEventHandler;
import com.appleframework.binlog.service.impl.BinLogTableMapEventHandler;
import com.appleframework.binlog.service.impl.BinLogUpdateEventHandler;
import com.appleframework.binlog.service.impl.BinLogWriteEventHandler;
import com.github.shyiko.mysql.binlog.event.EventHeader;
import com.github.shyiko.mysql.binlog.event.EventType;

/**
 * 根据类型提供事件的handler
 */
@Service
public class BinLogEventHandlerFactory {

	private static final Logger log = LoggerFactory.getLogger(BinLogDefaultEventHandler.class);

	@Resource
	private BinLogUpdateEventHandler binLogUpdateEventHandler;

	@Resource
	private BinLogWriteEventHandler binLogWriteEventHandler;

	@Resource
	private BinLogDeleteEventHandler binLogDeleteEventHandler;

	@Resource
	private BinLogDefaultEventHandler binLogDefaultEventHandler;

	@Resource
	private BinLogTableMapEventHandler binLogTableMapEventHandler;

	@Resource
	private BinLogRotateEventHandler binLogRotateEventHandler;

	public BinLogEventHandler getHandler(EventHeader header) {
		// 考虑到状态映射的问题，只在增删改是更新位置
		if (EventType.isUpdate(header.getEventType())) {
			return binLogUpdateEventHandler;
		} else if (EventType.isWrite(header.getEventType())) {
			return binLogWriteEventHandler;
		} else if (EventType.isDelete(header.getEventType())) {
			return binLogDeleteEventHandler;
		} else if (EventType.TABLE_MAP.equals(header.getEventType())) {
			log.debug("TableMapEvent-header:{}", header);
			return binLogTableMapEventHandler;
		} else if (EventType.ROTATE.equals(header.getEventType())) {
			log.debug("RotateEvent-header:{}", header);
			return binLogRotateEventHandler;
		} else {
			log.debug("不处理事件,{}", header);
			return binLogDefaultEventHandler;
		}
	}

	public void setBinLogUpdateEventHandler(BinLogUpdateEventHandler binLogUpdateEventHandler) {
		this.binLogUpdateEventHandler = binLogUpdateEventHandler;
	}

	public void setBinLogWriteEventHandler(BinLogWriteEventHandler binLogWriteEventHandler) {
		this.binLogWriteEventHandler = binLogWriteEventHandler;
	}

	public void setBinLogDeleteEventHandler(BinLogDeleteEventHandler binLogDeleteEventHandler) {
		this.binLogDeleteEventHandler = binLogDeleteEventHandler;
	}

	public void setBinLogDefaultEventHandler(BinLogDefaultEventHandler binLogDefaultEventHandler) {
		this.binLogDefaultEventHandler = binLogDefaultEventHandler;
	}

	public void setBinLogTableMapEventHandler(BinLogTableMapEventHandler binLogTableMapEventHandler) {
		this.binLogTableMapEventHandler = binLogTableMapEventHandler;
	}

	public void setBinLogRotateEventHandler(BinLogRotateEventHandler binLogRotateEventHandler) {
		this.binLogRotateEventHandler = binLogRotateEventHandler;
	}

}
