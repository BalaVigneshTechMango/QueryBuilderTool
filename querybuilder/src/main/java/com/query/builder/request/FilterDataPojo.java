package com.query.builder.request;

import java.util.List;

public class FilterDataPojo {

	private String tableNames;

	private String whereCondition;

	private String columnName;

	private String joinColumn;
	
	private List<String> columnNames;
	
	
	public String getJoinColumn() {
		return joinColumn;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public void setJoinColumn(String joinColumn) {
		this.joinColumn = joinColumn;
	}

	public String getTableNames() {
		return tableNames;
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setTableNames(String tableNames) {
		this.tableNames = tableNames;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
