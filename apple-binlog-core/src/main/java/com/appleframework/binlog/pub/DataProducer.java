package com.appleframework.binlog.pub;

import com.appleframework.binlog.model.ProducerDataDTO;

public interface DataProducer {
	
	void produce(ProducerDataDTO data);
	
}
