package com.tm.querybuilder.dao;

import java.util.List;
import java.util.Map;

import com.tm.querybuilder.dto.FilterData;

public interface QueryBuilderDao {

	// This method will return the column And TableName of the database
	public Map<String, Map<String, String>> fetchColumnDetails(String schemaString);

	// This Api for dynamic join query for multiple tables
	public Map<String, Object> fetchResultData(Map<String, String> queryMap);
	
	// this method is to check the schema exist in the db.
	boolean schemaExistDetails(String schemaString);

	// In the schema check whether the table is exist or not. 
	boolean tablesInSchema(String schemaString);

	// get the data type of the column in where clause
	Map<String, Map<String, String>> getDataType(FilterData filterData);

	// This method will build the entire where group and where list.
	StringBuilder whereCondition(FilterData filterData);

	// In this method it validate the table in the schema
	boolean validateTable(String tableString, String schemaString);

	//In this method validate the column by using schema and table name using column list
	boolean validateColumns(List<String> columnsList, String tableString, String schemaString);


}
