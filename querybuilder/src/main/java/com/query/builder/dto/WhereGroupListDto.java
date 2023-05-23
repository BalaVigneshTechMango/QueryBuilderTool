package com.query.builder.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.query.builder.enums.LogicalCondition;

public class WhereGroupListDto {

    @Valid
	private List<WhereListDto> whereList;
	private LogicalCondition logicalCondition;

	public LogicalCondition getLogicalCondition() {
		return logicalCondition;
	}

	public void setLogicalCondition(LogicalCondition logicalCondition) {
		this.logicalCondition = logicalCondition;
	}

	public List<WhereListDto> getWhereList() {
		return whereList;
	}

	public void setWhereList(List<WhereListDto> whereConditon) {
		this.whereList = whereConditon;
	}

}
