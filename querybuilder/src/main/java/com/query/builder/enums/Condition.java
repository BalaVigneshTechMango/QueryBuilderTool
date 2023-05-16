package com.query.builder.enums;

public enum Condition {

	EQUAL("="),
    NOTEQUAL("!=");

    private final String operator;

    Condition(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
	
}
