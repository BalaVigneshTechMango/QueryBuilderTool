package com.query.builder.enums;

public enum JoinsTypes {
	
	INNERJOIN("INNER JOIN"),
	LEFTJOIN("LEFT JOIN"),
	RIGHTJOIN("RIGHT JOIN"),
	OUTERJOIN("OUTER JOIN"),
	CROSSJOIN("CROSS JOIN");

	private final String operator;

	JoinsTypes(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
}
