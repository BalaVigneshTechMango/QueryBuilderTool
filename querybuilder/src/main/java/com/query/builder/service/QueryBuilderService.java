package com.query.builder.service;

import java.util.List;
import java.util.Map;

import com.query.builder.common.MyObject;
import com.query.builder.request.BuilderRequestPojo;

public interface QueryBuilderService {

	// get All table Name in Database
	List<String> getTableNames();

	// get All column name in particular table
	public List<String> getColumnName();

	public List<MyObject> groupBy(BuilderRequestPojo builderRequestPojo);

	// This method will return the column And TableName of the database
	public Object getColumnAndTableName();

	// Get All Schemas using schemata (get db names)
	public List<String> getAllSchema();

	// Get the specific table column of the table with datatype
	Map<String, String> getColumnAndDatatypes();

	//Get the column and data(values) using table name and schemaName(db Name)
	List<Map<String, Object>> getColumnValues();

	//get Column and values of by using list of table Name
	List<Map<String, Object>> getColumnValueListOfTable(BuilderRequestPojo builderRequestPojo);

}
