package com.appleframework.binlog.status;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.appleframework.binlog.model.LogStatus;

@Service
public class MapLogStatusSync implements LogStatusSync {

	private static Map<Long, LogStatus> map = new HashMap<>();
	
	@Override
	public void updateBinaryLogStatus(Long serverId, Long binlogPosition) {
		System.out.println("----------reg" + binlogPosition);
		LogStatus logStatus = map.get(serverId);
		logStatus.setBinlogPosition(binlogPosition);
	}

	@Override
	public void updateBinaryLogStatus(Long serverId, String binlogFilename, Long binlogPosition) {
		LogStatus logStatus = map.get(serverId);
		logStatus.setBinlogPosition(binlogPosition);
		logStatus.setBinlogFilename(binlogFilename);
	}

	@Override
	public LogStatus getBinaryLogStatus(Long serverId) {
		return map.get(serverId);
	}

	@Override
	public void initBinaryLogStatus(Long serverId) {
		//map.clear();
	}
	
}
