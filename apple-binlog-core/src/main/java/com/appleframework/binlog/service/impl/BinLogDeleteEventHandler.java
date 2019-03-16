package com.appleframework.binlog.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.appleframework.binlog.enums.DatabaseEvent;
import com.appleframework.binlog.model.ColumnsTableMapEventData;
import com.appleframework.binlog.model.DeleteRowsDTO;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.service.BinLogEventHandler;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;

@Service
public class BinLogDeleteEventHandler extends BinLogEventHandler {
	
    @Override
    protected EventBaseDTO formatData(Event event) {
        DeleteRowsEventData d = event.getData();
        
        //添加表信息
        ColumnsTableMapEventData tableMapData = TABLE_MAP_ID.get(d.getTableId());
        if(filter(tableMapData.getDatabase(), tableMapData.getTable())) {
        	return null;
        }
        
        DeleteRowsDTO deleteRowsDTO = new DeleteRowsDTO();
        deleteRowsDTO.setEventType(DatabaseEvent.DELETE_ROWS);
        deleteRowsDTO.setDatabase(tableMapData.getDatabase());
        deleteRowsDTO.setTable(tableMapData.getTable());
        //添加列映射
        int[] includedColumns = d.getIncludedColumns().stream().toArray();
        deleteRowsDTO.setRowMaps(d.getRows().stream()
                .map(r -> convert(r,includedColumns,tableMapData)).collect(Collectors.toList()));
        return deleteRowsDTO;
    }

    /**
     * 转化格式
     * @param data
     * @param includedColumns
     * @param tableMapData
     * @return
     */
    private Map<String,Serializable> convert(Serializable[] data, int[] includedColumns, 
    		ColumnsTableMapEventData tableMapData){
        Map<String, Serializable> result = new HashMap<>();
        IntStream.range(0, includedColumns.length)
            .forEach(i -> result.put(tableMapData.getColumnNames().get(includedColumns[i]), data[i]));
        return result;
    }

}