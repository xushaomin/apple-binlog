package com.appleframework.binlog.factory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.appleframework.binlog.config.BinaryLogConfig;

public class DatasouceFactory implements FactoryBean<DataSource> {

	private static Logger logger = LoggerFactory.getLogger(DatasouceFactory.class);

	private DataSource dataSource;

	private String DS_URL_FORMAT = "jdbc:mysql://{0}:{1}/mysql?characterEncoding=UTF-8&autoReconnect=true";

	@Override
	public DataSource getObject() throws Exception {
		logger.info("begin init redis...");
		dataSource = new DataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername(BinaryLogConfig.getUsername());
		dataSource.setPassword(BinaryLogConfig.getPassword());
		dataSource.setUrl(getUrl());
		dataSource.setInitialSize(1);
		dataSource.setMinIdle(1);
		dataSource.setMaxIdle(5);
		dataSource.setMaxActive(10);
		// test();
		return dataSource;
	}

	public void test() {
		Connection connection;
		try {
			connection = dataSource.getConnection();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException("init redis error, can not get connection.");
		}
	}

	@Override
	public Class<?> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void destroy() {
		if (dataSource != null)
			dataSource.close();
	}

	public String getUrl() {
		return MessageFormat.format(DS_URL_FORMAT, BinaryLogConfig.getHost(), BinaryLogConfig.getPort());
	}

}
