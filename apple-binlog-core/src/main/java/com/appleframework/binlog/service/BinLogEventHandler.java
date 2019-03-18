package com.appleframework.binlog.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.model.ColumnsTableMapEventData;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.pub.DataPublisher;
import com.appleframework.binlog.service.impl.BinLogWriteEventHandler;
import com.appleframework.binlog.status.LogStatusSync;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;

public abstract class BinLogEventHandler {

	private static final Logger log = LoggerFactory.getLogger(BinLogWriteEventHandler.class);

	protected final static Map<Long, ColumnsTableMapEventData> TABLE_MAP_ID = new ConcurrentHashMap<>();
		
	@Resource
	protected LogStatusSync logStatusSync;

	@Resource
	protected DataPublisher dataPublisher;
	
	@Resource
	protected ClientService clientService;
	
	@Resource
	protected DataSource dataSource;
		
	protected ColumnsTableMapEventData getTableMap(Long tableId) {
		ColumnsTableMapEventData data = TABLE_MAP_ID.get(tableId);
		if (null == data) {
			log.info("tableId为{}的表映射数据不存在", tableId);
		}
		return data;
	}

	protected void setTableMap(Long tableId, ColumnsTableMapEventData data) {
		TABLE_MAP_ID.put(tableId, data);
	}
	
	public void setDataPublisher(DataPublisher dataPublisher) {
		this.dataPublisher = dataPublisher;
	}

	public void setLogStatusSync(LogStatusSync logStatusSync) {
		this.logStatusSync = logStatusSync;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 处理event
	 *
	 * @param event
	 */
	public void handle(Event event) {
		EventBaseDTO eventBaseDTO = formatData(event);
		if(null != eventBaseDTO) {
			publish(formatData(event));
			updateBinaryLogStatus(event.getHeader());
		}
	}

	/**
	 * 格式化参数格式
	 *
	 * @param event
	 * @return 格式化后的string
	 */
	protected abstract EventBaseDTO formatData(Event event);

	/**
	 * 发布信息
	 *
	 * @param data
	 */
	protected void publish(EventBaseDTO data) {
		if (data != null) {
			log.debug("推送信息,{}", data);
			dataPublisher.publish(data);
		}
	}

	/**
	 * 更新日志位置
	 *
	 * @param header
	 */
	protected void updateBinaryLogStatus(EventHeaderV4 header) {
		logStatusSync.updateBinaryLogStatus(BinaryLogConfig.getServerId(), header.getNextPosition());
	}
	
	/**
     * 筛选出关注某事件的应用列表
     * @param event
     * @return
     */
    protected boolean filter(String database, String table) {
    	if(clientService.isExistClient(database, table)) {
			return false;
		}
    	else {
    		return true;
    	}
    }
}