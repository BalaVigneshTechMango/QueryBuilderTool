package com.tm.querybuilder.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.tm.querybuilder.validation.NoWhitespaceList;

public class FilterData {

	@Valid
	private List<WhereGroupListDto> whereData;

	@NotBlank(message = "Enter the tableName")
	private String tableName;
	@NotBlank(message = "Enter the SchemaName")
	private String schemaName;

	@NotBlank(message = "Enter Schema Name")
	private String database;

	@NotEmpty(message = "String list cannot be empty")
	@Size(min = 1, message = "Minimum One column should be selected")
	@NoWhitespaceList
	private List<String> columnNames;

	
	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}




	public List<WhereGroupListDto> getWhereData() {
		return whereData;
	}

	public void setWhereData(List<WhereGroupListDto> whereData) {
		this.whereData = whereData;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

}
