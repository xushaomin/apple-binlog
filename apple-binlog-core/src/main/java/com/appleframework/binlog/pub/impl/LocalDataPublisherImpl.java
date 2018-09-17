package com.appleframework.binlog.pub.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.appleframework.binlog.enums.DatabaseEvent;
import com.appleframework.binlog.model.ClientInfo;
import com.appleframework.binlog.model.DeleteRowsDTO;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.model.ProducerDataDTO;
import com.appleframework.binlog.model.UpdateRow;
import com.appleframework.binlog.model.UpdateRowsDTO;
import com.appleframework.binlog.model.WriteRowsDTO;
import com.appleframework.binlog.pub.DataProducer;
import com.appleframework.binlog.pub.DataPublisher;

@Service
public class LocalDataPublisherImpl implements DataPublisher {

	private static final Logger log = LoggerFactory.getLogger(LocalDataPublisherImpl.class);

	@Resource
	private DataProducer dataProducer;

	@Override
	public void publish(EventBaseDTO data, Set<ClientInfo> clientInfos) {

		String eventDatabase = data.getDatabase();
		String eventTable = data.getTable();

		clientInfos.forEach(clientInfo -> {
			String database = clientInfo.getDatabaseName();
			String table = clientInfo.getTableName();

			if (eventDatabase.equals(database) && eventTable.equals(table)) {
				if (data instanceof UpdateRowsDTO) {
					UpdateRowsDTO updateData = (UpdateRowsDTO) data;
					List<UpdateRow> list = updateData.getRows();
					for (UpdateRow updateRow : list) {
						ProducerDataDTO dto = new ProducerDataDTO(DatabaseEvent.UPDATE_ROWS, database, table);
						dto.setData(updateRow.getAfterRowMap());
						dto.setBefore(updateRow.getBeforeRowMap());
						dataProducer.produce(dto);
					}
				} else if (data instanceof WriteRowsDTO) {
					WriteRowsDTO writeData = (WriteRowsDTO) data;
					List<Map<String, Serializable>> list = writeData.getRowMaps();
					for (Map<String, Serializable> mdata : list) {
						ProducerDataDTO dto = new ProducerDataDTO(DatabaseEvent.WRITE_ROWS, database, table);
						dto.setData(mdata);
						dataProducer.produce(dto);
					}
				} else if (data instanceof DeleteRowsDTO) {
					DeleteRowsDTO deleteData = (DeleteRowsDTO) data;
					List<Map<String, Serializable>> list = deleteData.getRowMaps();
					for (Map<String, Serializable> mdata : list) {
						ProducerDataDTO dto = new ProducerDataDTO(DatabaseEvent.DELETE_ROWS, database, table);
						dto.setData(mdata);
						dataProducer.produce(dto);
					}
				} else {
					log.debug(data.toString());
				}
			}

		});

	}

}