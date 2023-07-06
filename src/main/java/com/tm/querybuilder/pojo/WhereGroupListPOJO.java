package com.tm.querybuilder.pojo;

import java.util.List;

import javax.validation.Valid;

import com.tm.querybuilder.enums.LogicalCondition;

public class WhereGroupListPOJO {

    @Valid
	private List<WhereListPOJO> whereList;
	private LogicalCondition logicalCondition;

	public LogicalCondition getLogicalCondition() {
		return logicalCondition;
	}

	public void setLogicalCondition(LogicalCondition logicalCondition) {
		this.logicalCondition = logicalCondition;
	}

	public List<WhereListPOJO> getWhereList() {
		return whereList;
	}

	public void setWhereList(List<WhereListPOJO> whereConditon) {
		this.whereList = whereConditon;
	}

}
