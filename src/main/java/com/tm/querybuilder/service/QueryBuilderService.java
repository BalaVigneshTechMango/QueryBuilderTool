package com.tm.querybuilder.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.response.QueryResponsePojo;
@Service
public interface QueryBuilderService {

	// The method get request from the Builder request pojo to get the details of
	// table and column
	public Map<String, Map<String, String>> fetchColumnDetails(String schemaName);

	// By getting string as query in parameter based on the query in will execute.
	public List<Map<String, Object>> fetchResultData(String queryString);

	// This method build the query based on the request.
	String fetchQuery(FilterData filterData);

	// This method will check the schema name and table exist in dao.
	public QueryResponsePojo schemaExistDetails(String schemaString, String databaseString);

	// This method will check the schema and table and column in dao.
	QueryResponsePojo schemaDetailsExist(String schemaString,String tableName,List<String>columnList);
	
	Map<String, Map<String, String>> getDataType(FilterData filterData);


}
