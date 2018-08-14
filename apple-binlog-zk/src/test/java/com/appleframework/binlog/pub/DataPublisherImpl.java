package com.appleframework.binlog.pub;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.appleframework.binlog.model.ClientInfo;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.model.UpdateRow;
import com.appleframework.binlog.model.UpdateRowsDTO;

//@Service
public class DataPublisherImpl implements DataPublisher {
	
	private static Set<String> set = new HashSet<>();
	
	static {
		set.add("base:by_device");
		set.add("base:by_device_type");
		set.add("base:by_device_config");
	}

	@Override
	public void publish(EventBaseDTO data, Set<ClientInfo> clientInfos) {
		String database = data.getDatabase();
		String table = data.getTable();
		String key = database + ":" + table;
		if(!set.contains(key)) {
			return;
		}
		
		System.out.println(data.getTable());
		if(data instanceof UpdateRowsDTO) {
			UpdateRowsDTO updateData = (UpdateRowsDTO)data;
			
			List<UpdateRow> list = updateData.getRows();
			for (UpdateRow updateRow : list) {
				System.out.println(updateRow.getAfterRowMap());
			}
		}
		System.out.println(data);
		System.out.println(data.getDatabase());
		System.out.println(data.getTable());
		System.out.println(data.getEventType().name());
	}
	
}
