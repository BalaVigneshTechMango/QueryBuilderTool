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
import com.tm.querybuilder.dto.ColumnDatatype;
import com.tm.querybuilder.dto.ColumnDetails;

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
	 * @param tableString
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
			LOGGER.error("An error occurred while checking if the isValid Column");
			throw new DataAccessResourceFailureException("An error occurred while checking if the isValid Column",
					exception);
		}
		LOGGER.debug("is Valid column dao :{}", isValidColumn);
		return isValidColumn;
	}

	/**
	 * This method will return the column And TableName of the database
	 * 
	 * @param schemaString
	 * @param tableString
	 * @return
	 */
	@Override
	public List<ColumnDetails> fetchColumnDetails(String schemaString) {
		
		LOGGER.info("fetch column details dao");
		
		List<ColumnDetails> columnList;
		try {
			// Query to get column names and data types for each table
			String sqlString = QueryConstants.SCHEMA_DETAILS;
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			paramsObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			columnList = namedParameterJdbcTemplate.query(sqlString, paramsObj,
					new BeanPropertyRowMapper<>(ColumnDetails.class));
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch Column Details");
			throw new DataAccessResourceFailureException("An error occurred while fetch Column Details", exception);
		}
		LOGGER.debug("Column and datatype dao:{}", columnList);
		return columnList;

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
	public List<ColumnDatatype> getDataType(String schemaString, Set<String> tableList, Set<String> columnList) {

		LOGGER.info("get Datatype dao");
		List<ColumnDatatype> columnDetailsList;
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			String sqlString = QueryConstants.GET_DATATYPE;
			paramsObj.addValue(MessageConstants.SCHEMA_NAME, schemaString);
			paramsObj.addValue(MessageConstants.TABLE_NAME, tableList);
			paramsObj.addValue("columns", columnList);
			columnDetailsList = namedParameterJdbcTemplate.query(sqlString, paramsObj,
					new BeanPropertyRowMapper<>(ColumnDatatype.class));
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while getting the Datatype");
			throw new DataAccessResourceFailureException("An error occurred while getting the Datatype", exception);
		}
		LOGGER.debug("get DataType dao: {}", columnDetailsList);
		return columnDetailsList;

	}

}
