package com.appleframework.binlog.zk.status;

import com.appleframework.binlog.config.BinaryLogConfig;
import com.appleframework.binlog.model.LogStatus;
import com.appleframework.binlog.status.LogStatusSync;
import com.appleframework.binlog.zk.config.ZkConfig;
import com.appleframework.binlog.zk.election.ZkClientUtil;

public class ZkLogStatusSync implements LogStatusSync {
	
	private static String BINLOG_FILENAME = "binlogFilename";
	private static String BINLOG_POSITION = "binlogPosition";
		
	public void init() {
		Long serverId = BinaryLogConfig.getServerId();
		String binlogFilenamePath = getBinlogFilenamePath(serverId);
		String binlogPositionPath = getBinlogPositionPath(serverId);
		
		if(!ZkClientUtil.exist(ZkConfig.getZkClientInfo(), binlogFilenamePath)) {
			ZkClientUtil.createPersistent(ZkConfig.getZkClientInfo(), binlogFilenamePath, "");
		}
		if(!ZkClientUtil.exist(ZkConfig.getZkClientInfo(), binlogPositionPath)) {
			ZkClientUtil.createPersistent(ZkConfig.getZkClientInfo(), binlogPositionPath, "0");
		}
	}
	
	private String getBinlogPositionPath(Long serverId) {
		return serverId + "/" + BINLOG_POSITION;
	}
	
	private String getBinlogFilenamePath(Long serverId) {
		return serverId + "/" + BINLOG_FILENAME;
	}

	@Override
	public void updateBinaryLogStatus(Long serverId, Long binlogPosition) {
		ZkClientUtil.update(ZkConfig.getZkClientInfo(), getBinlogPositionPath(serverId), binlogPosition.toString());
	}

	@Override
	public void updateBinaryLogStatus(Long serverId, String binlogFilename, Long binlogPosition) {
		ZkClientUtil.update(ZkConfig.getZkClientInfo(), getBinlogFilenamePath(serverId), binlogFilename);
		ZkClientUtil.update(ZkConfig.getZkClientInfo(), getBinlogPositionPath(serverId), binlogPosition.toString());
	}

	@Override
	public LogStatus getBinaryLogStatus(Long serverId) {
		String binlogFilename = ZkClientUtil.getData(ZkConfig.getZkClientInfo(), getBinlogFilenamePath(serverId));
		String binlogPosition = ZkClientUtil.getData(ZkConfig.getZkClientInfo(), getBinlogPositionPath(serverId));
		if(null == binlogPosition) {
			binlogPosition = "0";
		}
		return LogStatus.create(binlogFilename, Long.parseLong(binlogPosition));
	}

	@Override
	public void initBinaryLogStatus(Long serverId) {
		ZkClientUtil.update(ZkConfig.getZkClientInfo(), getBinlogFilenamePath(serverId), "");
		ZkClientUtil.update(ZkConfig.getZkClientInfo(), getBinlogPositionPath(serverId), "0");
	}
	
}
