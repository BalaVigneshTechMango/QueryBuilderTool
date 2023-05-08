package com.query.builder.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.query.builder.request.BuilderRequestPojo;

public interface QueryBuilderDao {

	// 1.get All table Name in Database
	public List<Map<String, Object>> getTableNames(String schemaName);

	// 2.Get the list list of table name and response it back as table wise select
	// Query
	public List<Map<String, Object>> listOfSelectQuery(List<String> tableName,String schemaName);

	// 3.This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getColumnAndTableName(String schemaName);

	List<Map<String, Object>> getColumnListOfTableName(String projectName, List<String> tableNames);

	List<Map<String, Object>> groupBy(List<String> columnNames, String schemaName, String tableName);

	// Get the specific table column of the table with datatype
	public Map<String, String> getColumnAndDatatypes(String tableName);

	// Get the column and data(values) using table name and schemaName(db Name)
	public List<Map<String, Object>> getColumnValues(String schemaName, String tableName);

	// get the table column by datatype (using the datatype and tableName)
	List<Map<String, Object>> getTableDataByType(String tableName, String dataType);

	// get the Current database Name from project
	public List<String> getDatabaseName();

	// This Api is filter condition of the selected columns for the tables
	Object getJoinedData(BuilderRequestPojo builderRequestPojo) throws JsonMappingException, JsonProcessingException;

	// This Api for dynamic join query for multiple tables
	public List<Map<String, Object>> intFilterCondition(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> getColumnValueDatatype(List<String> listTableName, String schemaName);

	//6.This API is used to join the table with using inner join without on condition and where conditon
	public List<Map<String, Object>> innerJoin(List<String> tableName);

	// not in use

	// get All column name in particular table
	public List<String> getColumnName(String schemaName, String tableName);

	public List<Map<String, Object>> executeDynamicQuery(List<String> tables, List<String> joins,
			String filterCondition);

	// Get All Schemas using schemata (get db names)
	public List<String> getAllSchemas();

}
