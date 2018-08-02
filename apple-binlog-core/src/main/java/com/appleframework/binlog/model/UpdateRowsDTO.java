package com.appleframework.binlog.model;

import java.util.List;

import com.appleframework.binlog.enums.DatabaseEvent;

public class UpdateRowsDTO extends EventBaseDTO {
	
	private static final long serialVersionUID = 1L;

    private List<UpdateRow> rows;

    public UpdateRowsDTO() {
    }

    public UpdateRowsDTO(EventBaseDTO eventBaseDTO, List<UpdateRow> rows) {
        super(eventBaseDTO);
        super.setEventType(DatabaseEvent.UPDATE_ROWS);
        this.rows = rows;
    }

    public List<UpdateRow> getRows() {
        return rows;
    }

    public void setRows(List<UpdateRow> rows) {
        this.rows = rows;
    }

	@Override
	public String toString() {
		return "UpdateRowsDTO{" + "rows=" + rows + "} " + super.toString();
	}
}
