package com.appleframework.binlog.model;

import java.io.Serializable;
import java.util.Objects;

public class ClientInfo implements Serializable {

	private static final long serialVersionUID = 1L;

    /**
     * 关注的数据库名
     */
    private String databaseName;

    /**
     * 关注的表名
     */
    private String tableName;
    
    public ClientInfo() {
    }

    public ClientInfo(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientInfo)) return false;
        ClientInfo that = (ClientInfo) o;
        return
                Objects.equals(databaseName, that.databaseName) &&
                Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseName, tableName);
    }

    @Override
    public String toString() {
        return "{" +
                ", \"databaseName\":\"" + databaseName + '\"' +
                ", \"tableName\":\"" + tableName + '\"' +
                "\"}";
    }
}
