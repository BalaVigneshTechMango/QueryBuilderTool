package com.tm.querybuilder.pojo;

import com.tm.querybuilder.enums.AggregateTypes;

public class AggregateFunctionPOJO {

	private AggregateTypes aggregateTypes;

	private String columnName;

	public AggregateTypes getAggregateTypes() {
		return aggregateTypes;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setAggregateTypes(AggregateTypes aggregateFunction) {
		this.aggregateTypes = aggregateFunction;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
