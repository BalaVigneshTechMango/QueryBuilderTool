package com.query.builder.dto;

import javax.validation.constraints.NotBlank;

import com.query.builder.enums.Condition;
import com.query.builder.enums.LogicalCondition;
import com.query.builder.validation.ObjectNoWhiteSpace;

public class WhereListDto {

	@NotBlank(message = "Enter column")
	private String column;

   // @EnumValue(enumClass = Condition.class)
	private Condition condition;

	@ObjectNoWhiteSpace
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
