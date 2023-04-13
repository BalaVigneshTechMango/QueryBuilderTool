package com.query.builder.request;

import java.util.List;

public class JoinData {

	private List<String> columnNames;

	private List<JoinConditionDto> joinCondition;

	private String lsTableName;

	private String rsTableName;

	private String joinType;

	private String whereCondition;

	public String getWhereCondition() {
		return whereCondition;
	}

	public List<JoinConditionDto> getJoinCondition() {
		return joinCondition;
	}

	public void setJoinCondition(List<JoinConditionDto> joinCondition) {
		this.joinCondition = joinCondition;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public String getLsTableName() {
		return lsTableName;
	}

	public String getRsTableName() {
		return rsTableName;
	}

	public String getJoinType() {
		return joinType;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public void setLsTableName(String lsTableName) {
		this.lsTableName = lsTableName;
	}

	public void setRsTableName(String rsTableName) {
		this.rsTableName = rsTableName;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

}
