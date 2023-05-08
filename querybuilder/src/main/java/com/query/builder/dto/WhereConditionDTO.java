package com.query.builder.dto;

public class WhereConditionDTO {

	private String columnName;
	private String operator;
	private String value;

	public String getColumnName() {
		return columnName;
	}

	public String getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
