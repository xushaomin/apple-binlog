package com.appleframework.binlog.model;

import java.io.Serializable;
import java.util.Map;

public class UpdateRow implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private Map<String, Serializable> beforeRowMap;
    private Map<String, Serializable> afterRowMap;

    public UpdateRow() {

    }

    public UpdateRow(Map<String, Serializable> beforeRowMap, Map<String, Serializable> afterRowMap) {
        this.beforeRowMap = beforeRowMap;
        this.afterRowMap = afterRowMap;
    }

    public Map<String, Serializable> getBeforeRowMap() {
        return beforeRowMap;
    }

    public void setBeforeRowMap(Map<String, Serializable> beforeRowMap) {
        this.beforeRowMap = beforeRowMap;
    }

    public Map<String, Serializable> getAfterRowMap() {
        return afterRowMap;
    }

    public void setAfterRowMap(Map<String, Serializable> afterRowMap) {
        this.afterRowMap = afterRowMap;
    }

    @Override
    public String toString() {
        return "UpdateRow{" +
                "beforeRowMap=" + beforeRowMap +
                ", afterRowMap=" + afterRowMap +
                '}';
    }
}
