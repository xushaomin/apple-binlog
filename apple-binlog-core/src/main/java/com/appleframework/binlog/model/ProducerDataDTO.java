package com.appleframework.binlog.model;

import java.io.Serializable;
import java.util.Map;

import com.appleframework.binlog.enums.DatabaseEvent;

public class ProducerDataDTO extends EventBaseDTO {
	
	private static final long serialVersionUID = 1L;

	private Map<String, Serializable> before;
	private Map<String, Serializable> data;

	public ProducerDataDTO() {
		super();
	}

	public ProducerDataDTO(DatabaseEvent eventType, String database, String table) {
		super(eventType, database, table);
	}

	public ProducerDataDTO(EventBaseDTO eventBaseDTO) {
		super(eventBaseDTO);
	}
	
	public Map<String, Serializable> getData() {
		return data;
	}

	public void setData(Map<String, Serializable> data) {
		this.data = data;
	}

	public Map<String, Serializable> getBefore() {
		return before;
	}

	public void setBefore(Map<String, Serializable> before) {
		this.before = before;
	}
    
}
