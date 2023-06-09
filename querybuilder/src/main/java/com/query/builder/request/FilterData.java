package com.query.builder.request;

import java.util.List;

public class FilterData {

	private String tableName;
	private List<WhereCondition> whereCondition;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<WhereCondition> getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(List<WhereCondition> whereCondition) {
		this.whereCondition = whereCondition;
	}
}
