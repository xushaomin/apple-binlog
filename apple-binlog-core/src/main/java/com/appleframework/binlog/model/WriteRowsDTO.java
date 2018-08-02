package com.appleframework.binlog.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.appleframework.binlog.enums.DatabaseEvent;

public class WriteRowsDTO extends EventBaseDTO {

	private static final long serialVersionUID = 1L;
	
	private List<Map<String, Serializable>> rowMaps;

    public WriteRowsDTO() {
    }

    public WriteRowsDTO(EventBaseDTO eventBaseDTO, List<Map<String, Serializable>> rowMaps) {
        super(eventBaseDTO);
        super.setEventType(DatabaseEvent.WRITE_ROWS);
        this.rowMaps = rowMaps;
    }

    public List<Map<String, Serializable>> getRowMaps() {
        return rowMaps;
    }

    public void setRowMaps(List<Map<String, Serializable>> rowMaps) {
        this.rowMaps = rowMaps;
    }

	@Override
	public String toString() {
		return "WriteRowsDTO{" + "rowMaps=" + rowMaps + "} " + super.toString();
	}
}
