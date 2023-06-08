package com.tm.querybuilder.service;

import java.util.Map;

import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.request.BuilderRequestPojo;
import com.tm.querybuilder.response.QueryResponsePojo;

public interface QueryBuilderService {

	// The method get request from the Builder request pojo to get the details of
	// table and column
	public Map<String, Map<String, String>> fetchColumnDetails(BuilderRequestPojo builderRequestPojo);

	// By getting string as query in parameter based on the query in will execute.
	public Map<String, Object> fetchResultData(Map<String, String> queryMap);

	// This method build the query based on the request.
	Map<String, String> fetchQuery(BuilderRequestPojo builderRequestPojo);

	// This method will check the schema name and table exist in dao.
	public QueryResponsePojo schemaExistDetails(String schemaString, String databaseString);

	// This method will check the schema and table and column in dao.
	QueryResponsePojo schemaDetailsExist(FilterData filterData);

}
