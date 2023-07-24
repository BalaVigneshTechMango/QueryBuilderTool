package com.tm.querybuilder.pojo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.tm.querybuilder.validation.NoWhitespaceList;

public class FilterDataPOJO {


	@NotBlank(message = "Enter the tableName")
	private String tableName;

	@NotEmpty(message = "String list cannot be empty")
	@Size(min = 1, message = "Minimum One column should be selected")
	@NoWhitespaceList
	private List<String> columnNames;
	
	private List<OrderByPOJO>orderBy;
	
	private GroupByPOJO groupBy;
	
	private WhereConditionPOJO whereData;
	
	@Valid
	private List<JoinDataPOJO> join;

	private int limit;

	private int pageNo;
	

	public WhereConditionPOJO getWhereData() {
		return whereData;
	}

	public void setWhereData(WhereConditionPOJO wheredata) {
		this.whereData = wheredata;
	}


	public GroupByPOJO getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(GroupByPOJO groupBy) {
		this.groupBy = groupBy;
	}

	public List<OrderByPOJO> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<OrderByPOJO> orderBy) {
		this.orderBy = orderBy;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public List<JoinDataPOJO> getJoin() {
		return join;
	}

	public void setJoin(List<JoinDataPOJO> join) {
		this.join = join;
	}

	

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

}
