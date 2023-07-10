package com.tm.querybuilder.dto;

public class TableRecordCount {

    private String tableName;
    private int recordCount;

    public TableRecordCount(String tableName, int recordCount) {
        this.tableName = tableName;
        this.recordCount = recordCount;
    }

	public String getTableName() {
		return tableName;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
    
    
	
}
