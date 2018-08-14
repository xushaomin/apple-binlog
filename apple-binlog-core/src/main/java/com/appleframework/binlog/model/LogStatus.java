package com.appleframework.binlog.model;

import java.io.Serializable;

public class LogStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	private String binlogFilename;
	private Long binlogPosition;

	public LogStatus() {
		super();
	}

	public LogStatus(String binlogFilename, Long binlogPosition) {
		super();
		this.binlogFilename = binlogFilename;
		this.binlogPosition = binlogPosition;
	}

	public String getBinlogFilename() {
		return binlogFilename;
	}

	public void setBinlogFilename(String binlogFilename) {
		this.binlogFilename = binlogFilename;
	}

	public Long getBinlogPosition() {
		return binlogPosition;
	}

	public void setBinlogPosition(Long binlogPosition) {
		this.binlogPosition = binlogPosition;
	}
	
	public static LogStatus create(String binlogFilename, Long binlogPosition) {
		return new LogStatus(binlogFilename, binlogPosition);
	}

}