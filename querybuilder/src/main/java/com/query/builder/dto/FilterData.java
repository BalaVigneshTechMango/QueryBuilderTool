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
	private LinkedList<WhereGroupListDto> whereGroupList;

	@NotBlank(message = "Enter the tableName")
	private String tableName;

	@NotEmpty(message = "String list cannot be empty")
	@Size(min = 1, message = "Minimum One column should be selected")
	@NoWhitespaceList
	private List<String> columnNames;

	public LinkedList<WhereGroupListDto> getWhereGroupList() {
		return whereGroupList;
	}

	public void setWhereGroupList(LinkedList<WhereGroupListDto> whereGroupList) {
		this.whereGroupList = whereGroupList;
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
