package com.tm.querybuilder.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tm.querybuilder.dto.FilterData;
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
	public Integer schemaExistDetails(String schemaString);

	// This method will check the schema and table and column in dao.
	boolean validateTable(String schemaString,String tableName);
	
	Map<String, Map<String, String>> getDataType(FilterData filterData);

	boolean validateColumns(List<String> columnList, String tableName, String schemaString);


}
