package com.query.builder.dto;

import java.util.List;

import javax.persistence.criteria.JoinType;

import com.query.builder.enums.JoinsTypes;

public class FilterDataPojo {

	//Filter
	private List<WhereGroupListDto> whereGroupList;

	private String tableName;

	private List<String> columnNames;

	// joins

	private List<JoinConditionDto> joinCondition;

	private String lsTableName;

	private String rsTableName;


	private String whereCondition;

	private boolean isActive;
	

	private JoinsTypes joinsTypes;



	public JoinsTypes getJoinsTypes() {
		return joinsTypes;
	}

	public void setJoinsTypes(JoinsTypes joinsTypes) {
		this.joinsTypes = joinsTypes;
	}

	public List<JoinConditionDto> getJoinCondition() {
		return joinCondition;
	}

	public String getLsTableName() {
		return lsTableName;
	}

	public String getRsTableName() {
		return rsTableName;
	}


	public String getWhereCondition() {
		return whereCondition;
	}

	public void setJoinCondition(List<JoinConditionDto> joinCondition) {
		this.joinCondition = joinCondition;
	}

	public void setLsTableName(String lsTableName) {
		this.lsTableName = lsTableName;
	}

	public void setRsTableName(String rsTableName) {
		this.rsTableName = rsTableName;
	}

	

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getTableName() {
		return tableName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<WhereGroupListDto> getWhereGroupList() {
		return whereGroupList;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setWhereGroupList(List<WhereGroupListDto> whereGroupList) {
		this.whereGroupList = whereGroupList;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

}
