package com.tm.querybuilder.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tm.querybuilder.dto.ColumnDatatypeDTO;
import com.tm.querybuilder.dto.ColumnDetailsDTO;

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
	public Boolean isSchemaExist(String schemaString);

	/**
	 * In this method it validate the table in the schema
	 * 
	 * @param schemaString
	 * @param tableString
	 * @return
	 */
	public Boolean isValidTable(String schemaString, Set<String> tableList);

	/**
	 * In this method validate the column by using schema and table name using
	 * column list
	 * 
	 * @param columnsList
	 * @param tableString
	 * @param schemaString
	 * @return
	 */
	public Boolean isValidColumns(Set<String> columnsList, Set<String> tableList, String schemaString);

	/**
	 * get the data type of the column in where clause
	 * 
	 * @param schemaString
	 * @param tableName
	 * @param columnList
	 * @return
	 */
	public List<ColumnDatatypeDTO> getDataType(String schemaString, Set<String> tableList, Set<String> columnList);


	/**
	 * This method will return the column And TableName of the database
	 * 
	 * @param schemaString
	 * @param tableString
	 * @return
	 */
	List<ColumnDetailsDTO> fetchColumnDetails(String schemaString);

	
	

}
