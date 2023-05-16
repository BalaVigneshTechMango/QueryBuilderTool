package com.query.builder.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.query.builder.request.BuilderRequestPojo;

public interface QueryBuilderService {

	// 1.get All table Name in Database
	List<Map<String, Object>> getTableNames(BuilderRequestPojo builderRequestPojo);

	// 2. Get the list list of table name and response it back as table wise select
	// Query
	List<Map<String, Object>> listOfSelectQuery(BuilderRequestPojo builderRequestPojo);

	// 3.This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getColumnAndTableName(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> getColumnListOfTableName(BuilderRequestPojo builderRequestPojo);

	public List<Map<String, Object>> groupBy(BuilderRequestPojo builderRequestPojo);

	// Get the specific table column of the table with datatype
	Map<String, String> getColumnAndDatatypes(BuilderRequestPojo builderRequestPojo);

	// Get the column and data(values) using table name and schemaName(db Name)
	List<Map<String, Object>> getColumnValues(BuilderRequestPojo builderRequestPojo);


	// get the table column by datatype (using the datatype and tableName)
	List<Map<String, Object>> getTableDataByType(BuilderRequestPojo builderRequestPojo);

	// get the Current database Name from project
	public List<String> getDataBaseName();

	// This Api is filter condition of the selected columns for the tables
	public List<Map<String, Object>> intFilterCondition(BuilderRequestPojo builderRequestPojo);

	// This Api for dynamic join query for multiple tables
	List<Map<String, Object>> getJoinData(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> getColumnValueDatatype(BuilderRequestPojo builderRequestPojo);

	//5.This API is used to join the table with using inner join without on condition and where conditon
	List<Map<String, Object>> innerJoin(BuilderRequestPojo builderRequestPojo);

	// not in use

	// get All column name in particular table
	public List<String> getColumnName(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> executeDynamicQuery(BuilderRequestPojo builderRequestPojo);

	// Get All Schemas using schemata (get db names)
	public List<String> getAllSchema();

	List<String> getPrimaryKeyAndIndexColumns(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> getJoinQuery(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> getFilterQuery(BuilderRequestPojo builderRequestPojo);

}
