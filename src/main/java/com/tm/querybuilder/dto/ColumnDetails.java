package com.tm.querybuilder.dto;

public class ColumnDetails {
	
	public String columnName;
	public String tableName;
	public String dataType;
	
	
	public String getColumnName() {
		return columnName;
	}
	public String getTableName() {
		return tableName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
