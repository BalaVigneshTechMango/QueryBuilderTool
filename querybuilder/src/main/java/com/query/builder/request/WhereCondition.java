package com.query.builder.request;
public class WhereCondition {
    private String[] filterColumn;
    private String columnName;
    private String operator;
    private Object value;
    private String logicalOperator;

    public String[] getFilterColumn() {
        return filterColumn;
    }

    public void setFilterColumn(String[] filterColumn) {
        this.filterColumn = filterColumn;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

	public String getOperator() {
		return operator;
	}

	public Object getValue() {
		return value;
	}

	public String getLogicalOperator() {
		return logicalOperator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}
}