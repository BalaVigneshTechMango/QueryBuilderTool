package com.tm.querybuilder.dao;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface QueryBuilderDao {

	/**
	 * This Api for dynamic join query for multiple tables
	 * 
	 * @param queryString
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> fetchResultData(String queryString);

	/**
	 * this method is to check the schema exist in the db.
	 * 
	 * @param schemaString
	 * @return boolean
	 */
	public boolean isSchemaExist(String schemaString);

	/**
	 * In this method it validate the table in the schema
	 * 
	 * @param schemaString
	 * @param tableString
	 * @return
	 */
	public boolean isValidateTable(String schemaString, String tableString);

	/**
	 * In this method validate the column by using schema and table name using
	 * column list
	 * 
	 * @param columnsList
	 * @param tableString
	 * @param schemaString
	 * @return
	 */
	public boolean isValidateColumns(List<String> columnsList, String tableString, String schemaString);

	/**
	 * get the data type of the column in where clause
	 * 
	 * @param schemaString
	 * @param tableName
	 * @param columnList
	 * @return
	 */
	public Map<String, Object> getDataType(String schemaString, String tableName, List<String> columnList);

	/**
	 * get the table details using schemaName
	 * 
	 * @param schemaName
	 * @return
	 */
	public List<String> fetchTableDetails(String schemaName);

	/**
	 * This method will return the column And TableName of the database
	 * 
	 * @param schemaString
	 * @param tableString
	 * @return
	 */
	public Map<String, Object> fetchColumnDetails(String schemaString, String tableString);

}
