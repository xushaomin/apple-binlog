package com.appleframework.binlog.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.appleframework.binlog.enums.DatabaseEvent;

public class EventBaseDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

    /**
     * 确保传输的数据唯一
     */
    private String uuid = UUID.randomUUID().toString();
    private DatabaseEvent eventType;
    private String database;
    private String table;

    public EventBaseDTO() {
    }

    public EventBaseDTO(DatabaseEvent eventType, String database, String table) {
        this.eventType = eventType;
        this.database = database;
        this.table = table;
    }

    public EventBaseDTO(EventBaseDTO eventBaseDTO) {
        this.eventType = eventBaseDTO.getEventType();
        this.database = eventBaseDTO.getDatabase();
        this.table = eventBaseDTO.getTable();
    }

    public DatabaseEvent getEventType() {
        return eventType;
    }

    public void setEventType(DatabaseEvent eventType) {
        this.eventType = eventType;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBaseDTO baseDTO = (EventBaseDTO) o;
        return Objects.equals(uuid, baseDTO.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "EventBaseDTO{" +
                "uuid='" + uuid + '\'' +
                ", eventType=" + eventType +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                '}';
    }
}
