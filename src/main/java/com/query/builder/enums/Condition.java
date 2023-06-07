package com.query.builder.enums;

public enum Condition {

	EQUAL("="), NE("!="), LT("<"), GT(">"), GTE(">="), LTE("<="), CONTAINS("CONTAINS"), LIKE("LIKE");

	private final String operator;

	Condition(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

}
