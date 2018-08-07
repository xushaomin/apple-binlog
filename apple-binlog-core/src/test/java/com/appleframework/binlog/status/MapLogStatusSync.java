package com.appleframework.binlog.status;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class MapLogStatusSync implements LogStatusSync {

	private static Map<String, String> map = new HashMap<>();
	
	@Override
	public void updateBinaryLogStatus(Long serverId, Long binlogPosition) {
		map.put("binlogPosition", binlogPosition.toString());
	}

	@Override
	public void updateBinaryLogStatus(Long serverId, String binlogFilename, Long binlogPosition) {
		map.put("binlogFilename", binlogFilename);
		map.put("binlogPosition", binlogPosition.toString());
	}

	@Override
	public Map<String, String> getBinaryLogStatus(Long serverId) {
		return map;
	}

	@Override
	public void initBinaryLogStatus(Long serverId) {
		map.clear();
	}
	
}
