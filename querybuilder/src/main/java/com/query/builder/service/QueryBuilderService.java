package com.query.builder.service;

import java.util.List;
import java.util.Map;

import com.query.builder.request.BuilderRequestPojo;

public interface QueryBuilderService {

	// get All table Name in Database
	List<Map<String, Object>> getTableNames(BuilderRequestPojo builderRequestPojo);

	// get All column name in particular table
	public List<String> getColumnName(BuilderRequestPojo builderRequestPojo);

	public List<Map<String, Object>> groupBy(BuilderRequestPojo builderRequestPojo);

	// This method will return the column And TableName of the database
	public List<Map<String, Object>> getColumnAndTableName(BuilderRequestPojo builderRequestPojo);

	// Get All Schemas using schemata (get db names)
	public List<String> getAllSchema();

	// Get the specific table column of the table with datatype
	Map<String, String> getColumnAndDatatypes(BuilderRequestPojo builderRequestPojo);

	// Get the column and data(values) using table name and schemaName(db Name)
	List<Map<String, Object>> getColumnValues(BuilderRequestPojo builderRequestPojo);

	// get Column and values of by using list of table Name
	List<Map<String, Object>> getColumnValueListOfTable(BuilderRequestPojo builderRequestPojo);

	// get the table column by datatype (using the datatype and tableName)
	List<Map<String, Object>> getTableDataByType(BuilderRequestPojo builderRequestPojo);

	// get the Current database Name from project
	public List<String> getDataBaseName();

	// This Api is filter condition of the selected columns for the tables
	public List<Map<String, Object>> intFilterCondition(BuilderRequestPojo builderRequestPojo);

	// This Api for dynamic join query for multiple tables
	List<Map<String, Object>> getJoinData(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> getColumnValueDatatype(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> executeDynamicQuery(BuilderRequestPojo builderRequestPojo);

}
