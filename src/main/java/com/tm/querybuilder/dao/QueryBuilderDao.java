package com.tm.querybuilder.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

@Service
public interface QueryBuilderDao {


	// This Api for dynamic join query for multiple tables
	public List<Map<String, Object>> fetchResultData(String queryMap);

	// this method is to check the schema exist in the db.
	Integer schemaExistDetails(String schemaString);

	// In this method it validate the table in the schema
	boolean validateTable(String tableString, String queryString);

	// In this method validate the column by using schema and table name using
	// column list
	boolean validateColumns(List<String> columnsList, String tableString, String schemaString);

	// get the data type of the column in where clause
	SqlRowSet getDataType(String schemaString, String tableName, List<String> columnList);

	SqlRowSet fetchTableDetails(String schemaName);

	// This method will return the column And TableName of the database
	SqlRowSet fetchColumnDetails(String schemaString, String tableString);

}
