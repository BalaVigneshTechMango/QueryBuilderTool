package com.tm.querybuilder.service;

import java.util.Map;

import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.request.BuilderRequestPojo;
import com.tm.querybuilder.response.QueryResponsePojo;

public interface QueryBuilderService {

	// The method get request from the Builder request pojo to get the details of
	// table and column
	public Map<String, Map<String, String>> getTableColumn(BuilderRequestPojo builderRequestPojo);

	// By getting string as query in parameter based on the query in will execute.
	public Map<String, Object> getQueryExecution(Map<String, String> queryMap);
	
	// This method build the query based on the request.
	Map<String, String> getQueryBuild(BuilderRequestPojo builderRequestPojo);

	// This method will check the schema name and table exist in dao.
	public QueryResponsePojo schemaCheck(String schemaString, String databaseString);

	// This method will check the schema and table and  column in dao.
	QueryResponsePojo schemaTableColumn(FilterData filterData);

}
