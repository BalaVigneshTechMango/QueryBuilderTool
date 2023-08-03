package com.tm.querybuilder.pojo;

import java.util.List;

public class GroupByPOJO {

	private List<String>columnList;
	private List<ConditionGroupPOJO>conditionData;
	
	public List<String> getColumnList() {
		return columnList;
	}
	public List<ConditionGroupPOJO> getConditionData() {
		return conditionData;
	}
	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}
	public void setConditionData(List<ConditionGroupPOJO> conditionGroup) {
		this.conditionData = conditionGroup;
	}
	
	
}
