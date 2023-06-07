package com.tm.querybuilder.service;

import java.util.Map;

import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.request.BuilderRequestPojo;
import com.tm.querybuilder.response.QueryResponsePojo;

public interface QueryBuilderService {

	// 3.This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getTableColumn(BuilderRequestPojo builderRequestPojo);

	// This is filter condition of the selected columns for the tables
	public Map<String, Object> getQueryExecution(Map<String, String> query);

	Map<String, String> getQueryBuild(BuilderRequestPojo builderRequestPojo);

	public QueryResponsePojo schemaCheck(String schemaName,String database);

	QueryResponsePojo schemaTableColumn(FilterData filterData);

}
