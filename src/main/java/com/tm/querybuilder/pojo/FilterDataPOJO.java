package com.tm.querybuilder.pojo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.tm.querybuilder.validation.NoWhitespaceList;

public class FilterDataPOJO {

	@Valid
	private List<WhereGroupListPOJO> whereData;

	@NotBlank(message = "Enter the tableName")
	private String tableName;

	@NotEmpty(message = "String list cannot be empty")
	@Size(min = 1, message = "Minimum One column should be selected")
	@NoWhitespaceList
	private List<String> columnNames;

	@Valid
	private List<JoinDataPOJO> join;
	
	public List<JoinDataPOJO> getJoin() {
		return join;
	}

	public void setJoin(List<JoinDataPOJO> join) {
		this.join = join;
	}

	public List<WhereGroupListPOJO> getWhereData() {
		return whereData;
	}

	public void setWhereData(List<WhereGroupListPOJO> whereData) {
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
