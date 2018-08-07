package com.appleframework.binlog.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.appleframework.binlog.enums.DatabaseEvent;
import com.appleframework.binlog.model.ColumnsTableMapEventData;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.model.UpdateRow;
import com.appleframework.binlog.model.UpdateRowsDTO;
import com.appleframework.binlog.service.BinLogEventHandler;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;

@Service
public class BinLogUpdateEventHandler extends BinLogEventHandler {

    @Override
    public EventBaseDTO formatData(Event event) {
        UpdateRowsEventData d = event.getData();
        UpdateRowsDTO updateRowsDTO = new UpdateRowsDTO();
        updateRowsDTO.setEventType(DatabaseEvent.UPDATE_ROWS);
        //添加表信息
        ColumnsTableMapEventData tableMapData = TABLE_MAP_ID.get(d.getTableId());
        updateRowsDTO.setDatabase(tableMapData.getDatabase());
        updateRowsDTO.setTable(tableMapData.getTable());
        //添加列映射
        int[] includedColumns = d.getIncludedColumns().stream().toArray();
        List<UpdateRow> urs = d.getRows().stream()
                .map(e -> new UpdateRow(convert(e.getKey(),includedColumns,tableMapData),
                        convert(e.getValue(),includedColumns,tableMapData))).collect(Collectors.toList());
        updateRowsDTO.setRows(urs);
        return updateRowsDTO;
    }

    /**
     * 转化格式
     * @param data
     * @param includedColumns
     * @param tableMapData
     * @return
     */
    private Map<String,Serializable> convert(Serializable[] data,int[] includedColumns,
    		ColumnsTableMapEventData tableMapData){
        Map<String, Serializable> result = new HashMap<>();
        if(includedColumns.length > 0) {
            IntStream.range(0, includedColumns.length)
            	.forEach(i -> result.put(tableMapData.getColumnNames().get(includedColumns[i]), data[i]));
        }
        return result;
    }

}