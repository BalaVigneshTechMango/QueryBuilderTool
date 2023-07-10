package com.tm.querybuilder.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.constant.MessageConstants;
import com.tm.querybuilder.constant.QueryConstants;
import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.ColumnDatatypeDTO;
import com.tm.querybuilder.dto.ColumnDetailsDTO;
import com.tm.querybuilder.dto.TableRecordCount;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryBuilderDaoImpl.class);

	/**
	 * This method will check the schema name and table exist in dao.
	 * 
	 * @param schemaString
	 * @return
	 */
	@Override
	public Boolean isSchemaExist(String schemaString) {
		LOGGER.info("Is Schema Exist Dao layer.");
		boolean isSchemaExist = false;
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			// Build a query and store in string.
			String existsSqlString = QueryConstants.IS_SCHEMA_EXIST;
			paramsObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			isSchemaExist = namedParameterJdbcTemplate.queryForObject(existsSqlString, paramsObj, Boolean.class);
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while checking if the schema exists", exception);
			throw new DataAccessResourceFailureException("An error occurred while checking if the schema exists");
		}
		LOGGER.debug("Schema Exist dao method data:{}", isSchemaExist);
		return isSchemaExist;
	}

	/**
	 * In this method it validate the table in the schema
	 * 
	 * @param schemaString
	 * @param tableList
	 * @return
	 */
	@Override
	public Boolean isValidTable(String schemaString, Set<String> tableList) {

		LOGGER.info("isValid Table Dao");
		boolean isValidTable = false;
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			// Build a query and store in string.
			String queryString = QueryConstants.IS_VALID_TABLE;
			parametersObj.addValue(MessageConstants.TABLE_NAME, tableList);
			parametersObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
			isValidTable = countInt != null && countInt == tableList.size();
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while checking if the isValid Table", exception);
			throw new DataAccessResourceFailureException("An error occurred while checking if the isValid Table");
		}
		LOGGER.debug("is valid table dao:{}", isValidTable);
		return isValidTable;
	}

	/**
	 * In this method validate the column by using schema and table name using
	 * column list
	 * 
	 * @param columnsList
	 * @param tableList
	 * @param schemaString
	 * @return
	 */
	@Override
	public Boolean isValidColumns(Set<String> columnsList, Set<String> tableList, String schemaString) {

		LOGGER.info("Is Valid Columns Dao");
		boolean isValidColumn = false;
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			String queryString = QueryConstants.IS_VALID_COLUMN;
			parametersObj.addValue("columns", columnsList);
			parametersObj.addValue(MessageConstants.TABLE_NAME, tableList);
			parametersObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
			// if the count not equal to null and colunt the column size and if the both
			// condition is okay the returns
			isValidColumn = countInt != null && countInt == columnsList.size();
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while checking if the isValid TableDetailPOJO");
			throw new DataAccessResourceFailureException("An error occurred while checking if the isValid TableDetailPOJO",
					exception);
		}
		LOGGER.debug("is Valid column dao :{}", isValidColumn);
		return isValidColumn;
	}

	/**
	 * This method will return the column And TableName of the database
	 * 
	 * @param schemaString
	 * @return
	 */
	@Override
	public List<ColumnDetailsDTO> fetchColumnDetails(String schemaString) {
		
		LOGGER.info("fetch column details dao");
		List<ColumnDetailsDTO> columnList;
		try {
			// Query to get column names and data types for each table
			String sqlString = QueryConstants.SCHEMA_DETAIL;
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			paramsObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			columnList = namedParameterJdbcTemplate.query(sqlString, paramsObj,
					new BeanPropertyRowMapper<>(ColumnDetailsDTO.class));
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch TableDetailPOJO Details");
			throw new DataAccessResourceFailureException("An error occurred while fetch TableDetailPOJO Details", exception);
		}
		LOGGER.debug("TableDetailPOJO and datatype dao:{}", columnList);
		return columnList;

	}

	/**
	 * This method will get the query in parameter and execute
	 * 
	 * @param queryString
	 * @return 
	 */
	@Override
	public List<Map<String, Object>> fetchResultData(String queryString) {

		LOGGER.info("fetch Result Data Dao");
		List<Map<String, Object>> responseList = new ArrayList<>();
		try {
			MapSqlParameterSource params = new MapSqlParameterSource();
			responseList = namedParameterJdbcTemplate.queryForList(queryString, params);
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch Result Data dao.");
			throw new DataAccessResourceFailureException("An error occurred while fetch Result Data.", exception);
		}
		LOGGER.debug("fetch Result Data dao:{}", responseList);
		return responseList;

	}

	/**
	 * get the data type of the column in where clause
	 * 
	 * @param schemaString
	 * @param tableList
	 * @param columnList
	 * @return
	 */
	@Override
	public List<ColumnDatatypeDTO> getDataType(String schemaString, Set<String> tableList, Set<String> columnList) {
		LOGGER.info("get Datatype dao");
		List<ColumnDatatypeDTO> columnDetailsList;
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			String sqlString = QueryConstants.GET_DATATYPE;
			paramsObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			paramsObj.addValue(MessageConstants.TABLE_NAME, tableList);
			paramsObj.addValue("columns", columnList);
			columnDetailsList = namedParameterJdbcTemplate.query(sqlString, paramsObj,
					new BeanPropertyRowMapper<>(ColumnDatatypeDTO.class));
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while getting the Datatype");
			throw new DataAccessResourceFailureException("An error occurred while getting the Datatype", exception);
		}
		LOGGER.debug("get DataType dao: {}", columnDetailsList);
		return columnDetailsList;
	}
	
	 @Override
	 public List<TableRecordCount> getRecordCountsForAllTables(String schemaString) {
		 List<String>tableName=new ArrayList<>();
		 tableName.add("mall_details");
		 tableName.add("store_details");
		 tableName.add("product_details");
	        String query = "SELECT CONCAT('ANALYZE TABLE ', table_schema, '.', table_name, ';') as analyzes,table_name,\n"
	        		+ "TABLE_ROWS\n"
	        		+ "FROM information_schema.tables\n"
	        		+ "WHERE table_schema = :schemaName\n"
	        		+ "  AND table_name IN (:tableName)";
	        MapSqlParameterSource params = new MapSqlParameterSource();
	        params.addValue("schemaName", schemaString);
	        params.addValue("tableName", tableName);

	        return namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> {
	            String tableNames = rs.getString("TABLE_NAME");
	            int recordCount = rs.getInt("TABLE_ROWS");
	            return new TableRecordCount(tableNames, recordCount);
	        });
	    }


}
