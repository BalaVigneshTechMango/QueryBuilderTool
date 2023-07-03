package com.tm.querybuilder.enums;

public enum Condition {

	EQUAL("="), NOT_EQUAL("<>"), LT("<"), GT(">"), GTE(">="), LTE("<="), CONTAINS("CONTAINS"), LIKE("LIKE");
    
	private final String operator;

	Condition(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

}
