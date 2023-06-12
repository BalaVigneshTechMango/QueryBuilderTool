package com.tm.querybuilder.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.constant.Constants;
import com.tm.querybuilder.dao.QueryBuilderDao;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method will check the schema name and table exist in dao.
	 * 
	 * @param schemaString
	 * @return
	 */
	@Override
	public boolean isSchemaExist(String schemaString) {
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			// Build a query and store in string.
			String existsSqlString = "SELECT count(table_schema) FROM information_schema.tables WHERE table_schema = :schemaName ";
			paramsObj.addValue(Constants.SCHEMA_NAME, schemaString);
			return namedParameterJdbcTemplate.queryForObject(existsSqlString, paramsObj, Boolean.class);
		} catch (DataAccessException exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/**
	 * In this method it validate the table in the schema
	 * 
	 * @param schemaString
	 * @param tableString
	 * @return
	 */
	@Override
	public boolean isValidateTable(String schemaString, String tableString) {
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			// Build a query and store in string.
			String queryString = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :tableName AND table_schema = :schemaName";
			parametersObj.addValue(Constants.TABLE_NAME, tableString);
			parametersObj.addValue(Constants.SCHEMA_NAME, schemaString);
			Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
			return countInt != null && countInt > 0;
		} catch (DataAccessException exception) {
			exception.printStackTrace();
			return false;
		}
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
	public boolean isValidateColumns(List<String> columnsList, String tableString, String schemaString) {
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			String queryString = " SELECT COUNT(*) FROM information_schema.columns WHERE column_name IN (:columns) AND table_name = :tableName AND "
					+ "table_schema = :schemaName";
			parametersObj.addValue("columns", columnsList);
			parametersObj.addValue(Constants.TABLE_NAME, tableString);
			parametersObj.addValue(Constants.SCHEMA_NAME, schemaString);
			Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
			// if the count not equal to null and colunt the column size and if the both
			// condition is okay the returns
			return countInt != null && countInt == columnsList.size();
		} catch (DataAccessException exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/**
	 * get the table details using schemaName
	 * 
	 * @param schemaName
	 * @return
	 */
	@Override
	public List<String> fetchTableDetails(String schemaString) {
		List<String> tableList = new ArrayList<>();
		MapSqlParameterSource params = new MapSqlParameterSource();
		try {
			String sqlString = "SELECT table_name FROM information_schema.tables WHERE table_schema = :schemaName";
			params.addValue(Constants.SCHEMA_NAME, schemaString);
			tableList = namedParameterJdbcTemplate.queryForList(sqlString, params, String.class);
			return tableList;
		} catch (DataAccessException exception) {
			exception.printStackTrace();
			return new ArrayList<>();
		}

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
		Map<String, Object> columnMap = new LinkedHashMap<>();
		try {
			// Query to get column names and data types for each table
			String sqlString = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = :schemaName AND "
					+ "table_name= :tableName";
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			paramsObj.addValue(Constants.SCHEMA_NAME, schemaString);
			paramsObj.addValue(Constants.TABLE_NAME, tableString);
			columnMap = namedParameterJdbcTemplate.queryForMap(sqlString, paramsObj);
			return columnMap;
		} catch (DataAccessException exception) {
			exception.printStackTrace();
			return new HashMap<>();
		}

	}

	/**
	 * This method will get the query in parameter and execute
	 * 
	 * @param queryString
	 * @return List<Map<String, Object>>
	 */
	@Override
	public List<Map<String, Object>> fetchResultData(String queryString) {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource();
			return namedParameterJdbcTemplate.queryForList(queryString, params);
		} catch (DataAccessException exception) {
			exception.printStackTrace();
			return new ArrayList<>();
		}

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
		Map<String, Object> columnMap = new LinkedHashMap<>();
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			String sqlString = "SELECT column_name, data_type " + "FROM information_schema.columns "
					+ "WHERE table_schema = :schemaName AND table_name = :tableName AND column_name IN (:column)";
			paramsObj.addValue(Constants.SCHEMA_NAME, schemaString);
			paramsObj.addValue(Constants.TABLE_NAME, tableName);
			paramsObj.addValue("column", columnList);
			columnMap = namedParameterJdbcTemplate.queryForMap(sqlString, paramsObj);

			return columnMap;
		} catch (DataAccessException exception) {
			exception.printStackTrace();
			return new HashMap<>();
		}

	}

}
