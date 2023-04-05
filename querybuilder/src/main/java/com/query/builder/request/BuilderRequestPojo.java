package com.query.builder.request;

import java.util.List;

public class BuilderRequestPojo {

	private String schemaName;

	private String tableName;

	private List<String> columnNames;
	
	private List<String> listTableName;
	
	

	public List<String> getListTableName() {
		return listTableName;
	}

	public void setListTableName(List<String> listTableName) {
		this.listTableName = listTableName;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
