package com.appleframework.binlog.status;

import com.appleframework.binlog.model.LogStatus;

public interface LogStatusSync {

	public void updateBinaryLogStatus(Long serverId, Long binlogPosition);
	
	public void updateBinaryLogStatus(Long serverId, String binlogFilename, Long binlogPosition);
	
	public LogStatus getBinaryLogStatus(Long serverId);
	
	public void initBinaryLogStatus(Long serverId);
}
