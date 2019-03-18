package com.appleframework.binlog.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.appleframework.binlog.model.ColumnsTableMapEventData;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.service.BinLogEventHandler;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;

/**
 * 处理TableMapEvent，主要是映射表名和id
 */
@Service
public class BinLogTableMapEventHandler extends BinLogEventHandler {

    private static final Logger log = LoggerFactory.getLogger(BinLogTableMapEventHandler.class);
    
	@Override
    public void handle(Event event) {
        TableMapEventData d = event.getData();
        log.debug("TableMapEventData:{}", d);
        ColumnsTableMapEventData tableMapEventData = getTableMap2(d.getTableId());
        //如果表结构有变化，重新设置映射信息
        if (tableMapEventData == null || !ColumnsTableMapEventData.checkEqual(d, tableMapEventData)) {
            log.warn("更新表映射：{} : {} : {}", d.getTableId(), d.getDatabase(), d.getTable());
            /*if(filter(d.getDatabase(), d.getTable())) {
            	return;
            }*/
            ColumnsTableMapEventData data = new ColumnsTableMapEventData(d);
            String sql = "show columns from `" + d.getTable() + "` from `" + d.getDatabase() + "`";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet resultSet = ps.executeQuery();) {
                while (resultSet.next()) {
                    data.addColumnName(resultSet.getString("Field"));
                }
            } catch (SQLException e) {
                log.error("获取表数据错误,sql语句为{}，异常如下:{}", sql, e);
            }
            //将表id和表映射
            setTableMap(d.getTableId(), data);
        }
    }

	@Override
	protected EventBaseDTO formatData(Event event) {
		return null;
	}
}
