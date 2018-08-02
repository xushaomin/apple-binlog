package com.appleframework.binlog.factory;

import java.sql.SQLException;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.appleframework.binlog.config.BinaryLogConfig;

public class DatasouceFactory implements FactoryBean<DruidDataSource> {

	private static Logger logger = LoggerFactory.getLogger(DatasouceFactory.class);

	private DruidDataSource dataSource;

	private String DS_URL_FORMAT = "jdbc:mysql://{0}:{1}/mysql?characterEncoding=UTF-8&autoReconnect=true";

	@Override
	public DruidDataSource getObject() throws Exception {
		logger.info("begin init redis...");
		dataSource = new DruidDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername(BinaryLogConfig.username);
		dataSource.setPassword(BinaryLogConfig.password);
		dataSource.setUrl(getUrl());
		dataSource.setInitialSize(5);
		dataSource.setMinIdle(1);
		dataSource.setMaxActive(10);
		dataSource.setFilters("stat");
		dataSource.setPoolPreparedStatements(false);
		// test();
		return dataSource;
	}

	public void test() {
		DruidPooledConnection connection;
		try {
			connection = dataSource.getConnection();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException("init redis error, can not get connection.");
		}
	}

	@Override
	public Class<?> getObjectType() {
		return DruidDataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void destroy() {
		if (dataSource != null)
			dataSource.close();
	}

	public String getUrl() {
		return MessageFormat.format(DS_URL_FORMAT, BinaryLogConfig.getHost(), BinaryLogConfig.getPort());
	}

}
