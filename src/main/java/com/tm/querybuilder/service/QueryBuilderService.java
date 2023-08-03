package com.tm.querybuilder.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tm.querybuilder.dto.ColumnDetailsDTO;
import com.tm.querybuilder.pojo.FilterDataPOJO;
import com.tm.querybuilder.pojo.JoinDataPOJO;

@Service
public interface QueryBuilderService {

	/**
	 * @param schemaString
	 * @return 
	 * Get the details of table and column get the details with dao layer.
	 */
	public List<ColumnDetailsDTO> fetchColumnDetails(String schemaString);

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
	public String fetchQuery(FilterDataPOJO filterData,String schemaString);


	
	/**
	 * @param schemaString
	 * @return
	 * This method will check the schema name and table exist in dao.
	 */
	public Boolean isSchemaExist(String schemaString);

	
	/**
	 * @param schemaString
	 * @param tableName
	 * @return
	 * This method will check the schema and table and column in dao.
	 */
	public Boolean isValidTable(String schemaString, String tableString ,List<JoinDataPOJO>joinDataList);


	/**
	 * @param columnList
	 * @param tableName
	 * @param schemaString
	 * get the column valid using columnList with its table and schema
	 */
	public Boolean isValidColumns(FilterDataPOJO filterData, String schemaString);

	

}
