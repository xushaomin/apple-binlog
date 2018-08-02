package com.appleframework.binlog.status;

import java.util.Map;

public interface LogStatusSync {

	public void updateBinaryLogStatus(Long serverId, Long binlogPosition);
	
	public void updateBinaryLogStatus(Long serverId, String binlogFilename, Long binlogPosition);
	
	public Map<String, Object> getBinaryLogStatus(Long serverId);
}
