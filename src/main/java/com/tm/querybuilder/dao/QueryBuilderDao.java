package com.tm.querybuilder.dao;

import java.util.List;
import java.util.Map;

import com.tm.querybuilder.dto.FilterData;

public interface QueryBuilderDao {

	// This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getTableColumn(String schemaName);

	// This Api for dynamic join query for multiple tables
	public Map<String, Object> getQueryExecution(Map<String, String> query);
	
	// this method is to check the schema exist in the db.
	boolean schemaExists(String schemaName);

	// In the schema check whether the table is exist or not. 
	boolean checkTablesExistInSchema(String schemaName);

	// get the data type of the column in where clause
	Map<String, Map<String, String>> getDataType(FilterData filterData);

	// This method will build the entire where group and where list.
	StringBuilder whereCondition(FilterData filterData);

	// In this method it validate the table in the schema
	boolean validateTableExists(String tableName, String schemaName);

	//In this method validate the column by using schema and table name using column list
	boolean validateColumnsExist(List<String> columns, String tableName, String schemaName);


}
