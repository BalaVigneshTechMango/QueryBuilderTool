package com.tm.querybuilder.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.tm.querybuilder.enums.Condition;
import com.tm.querybuilder.enums.LogicalCondition;

public class JoinConditionDto {

	@NotBlank(message = "Enter the LeftColumn")
	private String lsColumn;

	@NotBlank(message = "Enter the Right Column")
	private String rsColumn;

	@NotNull(message = "Enter the Condition")
	private Condition condition;

	private LogicalCondition logicalCondition;

	public String getLsColumn() {
		return lsColumn;
	}

	public String getRsColumn() {
		return rsColumn;
	}

	public Condition getCondition() {
		return condition;
	}

	public LogicalCondition getLogicalCondition() {
		return logicalCondition;
	}

	public void setLsColumn(String lsColumn) {
		this.lsColumn = lsColumn;
	}

	public void setRsColumn(String rsColumn) {
		this.rsColumn = rsColumn;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public void setLogicalCondition(LogicalCondition logicalCondition) {
		this.logicalCondition = logicalCondition;
	}

}
