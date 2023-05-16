package com.query.builder.dto;

import com.query.builder.enums.Condition;
import com.query.builder.enums.LogicalCondition;

public class JoinConditionDto {

	private String lsColumn;

	private String rsColumn;

	private Condition condition;

	private LogicalCondition logicalCondition;

	public LogicalCondition getLogicalCondition() {
		return logicalCondition;
	}

	public void setLogicalCondition(LogicalCondition logicalCondition) {
		this.logicalCondition = logicalCondition;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public String getLsColumn() {
		return lsColumn;
	}

	public String getRsColumn() {
		return rsColumn;
	}

	public void setLsColumn(String lsColumn) {
		this.lsColumn = lsColumn;
	}

	public void setRsColumn(String rsColumn) {
		this.rsColumn = rsColumn;
	}




}
