package com.tm.querybuilder.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tm.querybuilder.dto.FilterData;

@Service
public interface QueryBuilderService {

	/**
	 * @param schemaName
	 * @return 
	 *  get the details of table and column get the details with dao layer.
	 */
	public Map<String, Map<String, Object>> fetchColumnDetails(String schemaName);

	/**
	 * @param queryString
	 * @return List<Map<String, Object>> By getting string as query in parameter
	 *         based on the query in will execute.
	 */
	public List<Map<String, Object>> fetchResultData(String queryString);

	/**
	 * @param filterData
	 * @return String This method build the query based on the request.
	 */
	public String fetchQuery(FilterData filterData);


	
	/**
	 * @param schemaString
	 * @return
	 * This method will check the schema name and table exist in dao.
	 */
	public boolean isSchemaExist(String schemaString);

	
	/**
	 * @param schemaString
	 * @param tableName
	 * @return
	 * This method will check the schema and table and column in dao.
	 */
	public boolean isValidateTable(String schemaString, String tableName);

	/**
	 * @param filterData
	 * @return
	 * get the datatype of column in the whereClause
	 */
	public Map<String, Map<String, Object>> getDataType(FilterData filterData);

	/**
	 * @param columnList
	 * @param tableName
	 * @param schemaString
	 * get the column valid using columnList with its table and schema
	 */
	public boolean isValidateColumns(List<String> columnList, String tableName, String schemaString);

}
