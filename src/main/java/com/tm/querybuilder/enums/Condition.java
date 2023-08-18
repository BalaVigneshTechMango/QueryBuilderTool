package com.tm.querybuilder.enums;

public enum Condition {

	EQUAL("="), NOT_EQUAL("<>"), LT("<"), GT(">"), GTE(">="), LTE("<="),CONTAINS("LIKE"),
	BETWEEN("BETWEEN"),IN("IN"),ISNOTNULL(" IS NOT NULL"),ISNULL("IS NULL");
    
	private final String operator;

	Condition(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

}
