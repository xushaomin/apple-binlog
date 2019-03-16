package com.appleframework.binlog.pub;

import com.appleframework.binlog.model.EventBaseDTO;

public interface DataPublisher {
	
    void publish(EventBaseDTO data);
    
}
