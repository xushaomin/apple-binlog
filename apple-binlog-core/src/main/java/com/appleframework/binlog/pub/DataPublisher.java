package com.appleframework.binlog.pub;

import java.util.Set;

import com.appleframework.binlog.model.ClientInfo;
import com.appleframework.binlog.model.EventBaseDTO;

public interface DataPublisher {
	
    void publish(EventBaseDTO data, Set<ClientInfo> clientInfos);
    
}
