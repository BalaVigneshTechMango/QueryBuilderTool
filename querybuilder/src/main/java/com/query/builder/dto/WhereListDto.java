package com.query.builder.dto;

import java.util.List;

import com.query.builder.enums.Condition;
import com.query.builder.enums.LogicalCondition;

public class WhereListDto {

	//private List<WhereCondition> whereConditions;
	
	private String column;
	private Condition condition;
	private Object value;
	private LogicalCondition logicalCondition;

	public String getColumn() {
		return column;
	}

	public Condition getCondition() {
		return condition;
	}

	public Object getValue() {
		return value;
	}

	public LogicalCondition getLogicalCondition() {
		return logicalCondition;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setLogicalCondition(LogicalCondition logicalCondition) {
		this.logicalCondition = logicalCondition;
	}

}
