package com.tm.querybuilder.pojo;

import com.tm.querybuilder.enums.AggregateFunction;

public class FunctionsPOJO {

	private AggregateFunction aggregateFunction;
	private String column;

	public AggregateFunction getAggregateFunction() {
		return aggregateFunction;
	}

	public String getColumn() {
		return column;
	}

	public void setAggregateFunction(AggregateFunction aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	public void setColumn(String column) {
		this.column = column;
	}
}
