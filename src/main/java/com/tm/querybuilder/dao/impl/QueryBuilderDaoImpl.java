package com.tm.querybuilder.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.constant.MessageConstants;
import com.tm.querybuilder.dao.QueryBuilderDao;

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
			String existsSqlString = "SELECT count(table_schema) FROM information_schema.tables WHERE table_schema = :schemaName ";
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
	 * @param tableString
	 * @return
	 */
	@Override
	public Boolean isValidTable(String schemaString, String tableString) {

		LOGGER.info("isValid Table Dao");
		boolean isValidTable = false;
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			// Build a query and store in string.
			String queryString = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :tableName AND table_schema = :schemaName";
			parametersObj.addValue(MessageConstants.TABLE_NAME, tableString);
			parametersObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			isValidTable = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Boolean.class);
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while checking if the isValid Table",exception);
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
	 * @param tableString
	 * @param schemaString
	 * @return
	 */
	@Override
	public Boolean isValidColumns(List<String> columnsList, String tableString, String schemaString) {

		LOGGER.info("Is Valid Columns Dao");
		boolean isValidColumn = false;
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			String queryString = " SELECT COUNT(*) FROM information_schema.columns WHERE column_name IN (:columns) AND table_name = :tableName AND "
					+ "table_schema = :schemaName";
			parametersObj.addValue("columns", columnsList);
			parametersObj.addValue(MessageConstants.TABLE_NAME, tableString);
			parametersObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
			// if the count not equal to null and colunt the column size and if the both
			// condition is okay the returns
			isValidColumn = countInt != null && countInt == columnsList.size();
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while checking if the isValid Column");
			throw new DataAccessResourceFailureException("An error occurred while checking if the isValid Column",
					exception);
		}
		LOGGER.debug("is Valid column dao :{}", isValidColumn);
		return isValidColumn;
	}

	/**
	 * get the table details using schemaName
	 * 
	 * @param schemaName
	 * @return
	 */
	@Override
	public List<String> fetchTableDetails(String schemaString) {

		LOGGER.info("fetch Table Details Dao");
		List<String> tableList = new ArrayList<>();
		MapSqlParameterSource params = new MapSqlParameterSource();
		try {
			String sqlString = "SELECT table_name FROM information_schema.tables WHERE table_schema = :schemaName";
			params.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			tableList = namedParameterJdbcTemplate.queryForList(sqlString, params, String.class);
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch the table Details",exception);
			throw new DataAccessResourceFailureException("An error occurred while fetch the table Details");
		}
		LOGGER.debug("table List of the Schema dao:{}", tableList);
		return tableList;

	}

	/**
	 * This method will return the column And TableName of the database
	 * 
	 * @param schemaString
	 * @param tableString
	 * @return
	 */
	@Override
	public Map<String, Object> fetchColumnDetails(String schemaString, String tableString) {

		LOGGER.info("fetch column details dao");
		List<Map<String, Object>> columnMap = new ArrayList<>();
		Map<String, Object> columnDetails = new LinkedHashMap<>();
		try {
			// Query to get column names and data types for each table
			String sqlString = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = :schemaName AND "
					+ "table_name= :tableName";
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			paramsObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			paramsObj.addValue(MessageConstants.TABLE_NAME, tableString);
			columnMap = namedParameterJdbcTemplate.queryForList(sqlString, paramsObj);
			for (Map<String, Object> column : columnMap) {
				String columnName = (String) column.get("column_name");
				String dataType = (String) column.get("data_type");
				columnDetails.put(columnName, dataType);
			}

		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch Column Details");
			throw new DataAccessResourceFailureException("An error occurred while fetch Column Details", exception);

		}
		LOGGER.debug("Column and datatype dao:{}", columnDetails);
		return columnDetails;

	}

	/**
	 * This method will get the query in parameter and execute
	 * 
	 * @param queryString
	 * @return List<Map<String, Object>>
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
	 * @param tableName
	 * @param columnList
	 * @return
	 */
	@Override
	public Map<String, Object> getDataType(String schemaString, String tableName, List<String> columnList) {

		LOGGER.info("get Datatype dao");
		Map<String, Object> columnDetails = new HashMap<>();
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			String sqlString = "SELECT column_name, data_type " + "FROM information_schema.columns "
					+ "WHERE table_schema = :schemaName AND table_name = :tableName AND column_name IN (:column)";
			paramsObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			paramsObj.addValue(MessageConstants.TABLE_NAME, tableName);
			paramsObj.addValue("column", columnList);
			List<Map<String, Object>> columnMap = namedParameterJdbcTemplate.queryForList(sqlString, paramsObj);
			for (Map<String, Object> column : columnMap) {
				String columnName = (String) column.get("column_name");
				String dataType = (String) column.get("data_type");
				columnDetails.put(columnName, dataType);
			}

		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while getting the Datatype");
			throw new DataAccessResourceFailureException("An error occurred while getting the Datatype", exception);
		}
		LOGGER.debug("get DataType dao: {}", columnDetails);
		return columnDetails;

	}

}
