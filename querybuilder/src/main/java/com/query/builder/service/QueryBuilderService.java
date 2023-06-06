package com.query.builder.service;

import java.util.Map;

import com.query.builder.dto.FilterData;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.response.QueryResponsePojo;

public interface QueryBuilderService {

	// 3.This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getTableColumn(BuilderRequestPojo builderRequestPojo);

	// This is filter condition of the selected columns for the tables
	public Map<String, Object> getQueryExecution(Map<String, String> query);

	Map<String, String> getQueryBuild(BuilderRequestPojo builderRequestPojo);

	public QueryResponsePojo schemaCheck(String schemaName,String database);

	QueryResponsePojo schemaTableColumn(FilterData filterData);

}
