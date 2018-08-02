package com.appleframework.binlog.status;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class MapLogStatusSync implements LogStatusSync {

	private static Map<String, Object> map = new HashMap<>();
	
	@Override
	public void updateBinaryLogStatus(Long serverId, Long binlogPosition) {
		map.put("binlogPosition", binlogPosition);
	}

	@Override
	public void updateBinaryLogStatus(Long serverId, String binlogFilename, Long binlogPosition) {
		map.put("binlogFilename", binlogFilename);
		map.put("binlogPosition", binlogPosition);
	}

	@Override
	public Map<String, Object> getBinaryLogStatus(Long serverId) {
		return map;
	}	
	
}
