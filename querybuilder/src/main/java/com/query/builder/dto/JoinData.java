package com.query.builder.dto;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.query.builder.enums.JoinTypes;
import com.query.builder.validation.NoWhitespaceList;

public class JoinData {

	@Valid
	private LinkedList<JoinConditionDto> joinCondition;

	@Valid
	private LinkedList<WhereGroupListDto> whereData;

	@NotBlank(message = "Enter the Left TableName")
	private String lsTableName;

	@NotBlank(message = "Enter the Right TableName")
	private String rsTableName;

	private JoinTypes joinType;

	// @NotEmpty(message = "String list cannot be empty")
	@Size(min = 1, message = "Minimum One column should be selected")
	@NoWhitespaceList
	private List<String> columnNames;

	public LinkedList<WhereGroupListDto> getWhereData() {
		return whereData;
	}

	public void setWhereData(LinkedList<WhereGroupListDto> whereGroupList) {
		this.whereData = whereGroupList;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public LinkedList<JoinConditionDto> getJoinCondition() {
		return joinCondition;
	}

	public String getLsTableName() {
		return lsTableName;
	}

	public String getRsTableName() {
		return rsTableName;
	}

	public JoinTypes getJoinType() {
		return joinType;
	}

	public void setJoinCondition(LinkedList<JoinConditionDto> joinCondition) {
		this.joinCondition = joinCondition;
	}

	public void setLsTableName(String lsTableName) {
		this.lsTableName = lsTableName;
	}

	public void setRsTableName(String rsTableName) {
		this.rsTableName = rsTableName;
	}

	public void setJoinType(JoinTypes joinsTypes) {
		this.joinType = joinsTypes;
	}

}