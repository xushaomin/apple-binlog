package com.appleframework.binlog.service.impl;

import com.appleframework.binlog.model.ColumnsTableMapEventData;
import com.appleframework.binlog.model.EventBaseDTO;
import com.appleframework.binlog.service.BinLogEventHandler;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import javax.annotation.Resource;

/**
 * 处理TableMapEvent，主要是映射表名和id
 */
@Service
public class BinLogTableMapEventHandler extends BinLogEventHandler {

    private static final Logger log = LoggerFactory.getLogger(BinLogTableMapEventHandler.class);

    @Resource
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
    
	@Override
    public void handle(Event event) {
        TableMapEventData d = event.getData();
        log.debug("TableMapEventData:{}", d);
        ColumnsTableMapEventData tableMapEventData = TABLE_MAP_ID.get(d.getTableId());
        //如果表结构有变化，重新设置映射信息
        if (tableMapEventData == null || !ColumnsTableMapEventData.checkEqual(d, tableMapEventData)) {
            log.info("更新表映射：{}-{}", d.getDatabase(), d.getTable());
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
            TABLE_MAP_ID.put(d.getTableId(), data);
        }
    }

	@Override
	protected EventBaseDTO formatData(Event event) {
		return null;
	}
}
