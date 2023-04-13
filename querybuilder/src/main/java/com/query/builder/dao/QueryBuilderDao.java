package com.query.builder.dao;

import java.util.List;
import java.util.Map;

import com.query.builder.request.BuilderRequestPojo;

public interface QueryBuilderDao {

	// get All table Name in Database
	public List<String> getTableNames(String schemaName);

	// get All column name in particular table
	public List<String> getColumnName(String schemaName, String tableName);

	List<Map<String, Object>> groupBy(List<String> columnNames, String schemaName, String tableName);

	// This method will return the column And TableName of the database
	public Object getColumnAndTableName(String schemaName);

	// Get All Schemas using schemata (get db names)
	public List<String> getAllSchemas();

	// Get the specific table column of the table with datatype
	public Map<String, String> getColumnAndDatatypes(String tableName);

	// Get the column and data(values) using table name and schemaName(db Name)
	public List<Map<String, Object>> getColumnValues(String schemaName, String tableName);

	// get Column and values of by using list of table Name
	List<Map<String, Object>> getColumnValueListOfTable(List<String> tableNames, String schemaName);

	// get the table column by datatype (using the datatype and tableName)
	List<Map<String, Object>> getTableDataByType(String tableName, String dataType);

	// get the Current database Name from project
	public List<String> getDatabaseName();

	// This Api is filter condition of the selected columns for the tables
	List<Map<String, Object>> getJoinedData(BuilderRequestPojo builderRequestPojo);

	// This Api for dynamic join query for multiple tables
	public List<Map<String, Object>> intFilterCondition(BuilderRequestPojo builderRequestPojo);

}
