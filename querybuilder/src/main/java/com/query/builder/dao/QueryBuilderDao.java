package com.query.builder.dao;

import java.util.List;
import java.util.Map;

import com.query.builder.common.MyObject;

public interface QueryBuilderDao {

	// get All table Name in Database
	public List<String> getTableNames();

	// get All column name in particular table
	public List<String> getColumnName();

	List<MyObject> groupBy(List<String> columnNames);

	// This method will return the column And TableName of the database
	public Object getColumnAndTableName();

	// Get All Schemas using schemata (get db names)
	public List<String> getAllSchemas();

	// Get the specific table column of the table with datatype
	public Map<String, String> getColumnAndDatatypes();

	// Get the column and data(values) using table name and schemaName(db Name)
	public List<Map<String, Object>> getColumnValues();

	//get Column and values of by using list of table Name
	List<Map<String, Object>> getColumnValueListOfTable(List<String> tableNames);

}
