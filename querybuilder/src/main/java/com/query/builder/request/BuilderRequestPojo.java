package com.query.builder.request;

import java.util.List;

public class BuilderRequestPojo {

	private String schemaName;

	private String tableName;

	private String whereCondition;

	private String dataType;

	private String filterCondition;

	private List<String> columnNames;

	private List<String> listTableName;

	private List<String> joinColumn;

	private List<FilterPojo> filterPojos;

	private List<JoinData> joinData;

	private List<String> joins; // executeDynamicQuery

	public List<String> getJoins() {
		return joins;
	}

	public void setJoins(List<String> joins) {
		this.joins = joins;
	}

	public List<JoinData> getJoinData() {
		return joinData;
	}

	public void setJoinData(List<JoinData> joinData) {
		this.joinData = joinData;
	}

	public List<FilterPojo> getFilterPojos() {
		return filterPojos;
	}

	public void setFilterPojos(List<FilterPojo> filterPojos) {
		this.filterPojos = filterPojos;
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	public List<String> getJoinColumn() {
		return joinColumn;
	}

	public void setJoinColumn(List<String> joinColumn) {
		this.joinColumn = joinColumn;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

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
