package com.query.builder.dto;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.query.builder.validation.NoWhitespaceList;

public class FilterData {

	@Valid
	private LinkedList<WhereGroupListDto> whereData;

	@Valid
	private LinkedList<JoinData> joinDatas;

	@NotBlank(message = "Enter the tableName")
	private String tableName;
	@NotBlank(message = "Enter the SchemaName")
	private String schemaName;

	private String dataBase;

	@NotEmpty(message = "String list cannot be empty")
	@Size(min = 1, message = "Minimum One column should be selected")
	@NoWhitespaceList
	private List<String> columnNames;

	public String getDataBase() {
		return dataBase;
	}

	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public LinkedList<JoinData> getJoinDatas() {
		return joinDatas;
	}

	public void setJoinDatas(LinkedList<JoinData> joinDatas) {
		this.joinDatas = joinDatas;
	}

	public LinkedList<WhereGroupListDto> getWhereData() {
		return whereData;
	}

	public void setWhereData(LinkedList<WhereGroupListDto> whereData) {
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
