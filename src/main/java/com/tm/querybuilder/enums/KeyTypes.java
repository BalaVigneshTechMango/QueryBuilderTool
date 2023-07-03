package com.tm.querybuilder.enums;

public enum KeyTypes {

	PRIMARY_KEY("PRI"),FOREGIN_KEY("MUL");
	
	private final String operator;

	KeyTypes(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
}
