package com.appleframework.binlog.pub;

import org.springframework.stereotype.Service;

import com.appleframework.binlog.model.ProducerDataDTO;

@Service
public class LocalDataProducerImpl implements DataProducer {

	@Override
	public void produce(ProducerDataDTO data) {
		System.out.println(data.toString());
	}

}