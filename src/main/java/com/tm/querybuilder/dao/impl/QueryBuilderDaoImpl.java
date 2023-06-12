package com.tm.querybuilder.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import com.tm.querybuilder.dao.QueryBuilderDao;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String SCHEMA_NAME = "schemaName";
	private static final String TABLE_NAME = "tableName";

	// this method will check schema Name in database
	@Override
	public Integer schemaExistDetails(String schemaString) {
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			// Build a query and store in string.
			String existsSqlString = "SELECT count(table_schema) FROM information_schema.tables WHERE table_schema = :schemaName ";
			paramsObj.addValue(SCHEMA_NAME, schemaString);
			return namedParameterJdbcTemplate.queryForObject(existsSqlString, paramsObj, Integer.class);
		} catch (Exception exception) {
			exception.printStackTrace();
			return 0;
		}
	}

	// In this method it validate the table in the schema
	@Override
	public Boolean validateTable(String schemaString, String tableString) {
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			// Build a query and store in string.
			String queryString = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :tableName AND table_schema = :schemaName";
			parametersObj.addValue(TABLE_NAME, tableString);
			parametersObj.addValue(SCHEMA_NAME, schemaString);
			Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
			return countInt != null && countInt > 0;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	// In this method validate the column by using schema and table name using
	// column list
	@Override
	public boolean validateColumns(List<String> columnsList, String tableString, String schemaString) {
		try {
			MapSqlParameterSource parametersObj = new MapSqlParameterSource();
			String queryString = "SELECT COUNT(*) FROM information_schema.columns WHERE column_name IN (:columns) AND table_name = :tableName AND "
					+ "table_schema = :schemaName";
			parametersObj.addValue("columns", columnsList);
			parametersObj.addValue(TABLE_NAME, tableString);
			parametersObj.addValue(SCHEMA_NAME, schemaString);
			Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
			// if the count not equal to null and colunt the column size and if the both
			// condition is okay the returns
			return countInt != null && countInt == columnsList.size();
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}

	}

	@Override
	public SqlRowSet fetchTableDetails(String schemaString) {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource();
			String sqlString = "SELECT table_name FROM information_schema.tables WHERE table_schema = :schemaName";
			params.addValue(SCHEMA_NAME, schemaString);
			return namedParameterJdbcTemplate.queryForRowSet(sqlString, params);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	// This method will return the column And TableName of the database
	@Override
	public SqlRowSet fetchColumnDetails(String schemaString, String tableString) {
		try {
			// Query to get column names and data types for each table
			String sqlString = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = :schemaName AND "
					+ "table_name= :tableName";
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			paramsObj.addValue(SCHEMA_NAME, schemaString);
			paramsObj.addValue(TABLE_NAME, tableString);
			return namedParameterJdbcTemplate.queryForRowSet(sqlString, paramsObj);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

	}

	// This method will get the query in parameter and execute
	@Override
	public List<Map<String, Object>> fetchResultData(String queryString) {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource();
			return namedParameterJdbcTemplate.queryForList(queryString, params);
		} catch (Exception exception) {
			exception.printStackTrace();
			return new ArrayList<>();
		}

	}

	// get the data type of the column in where clause
	@Override
	public SqlRowSet getDataType(String schemaString, String tableName, List<String> columnList) {
		try {
			MapSqlParameterSource paramsObj = new MapSqlParameterSource();
			// Build a query and store in string
			String sqlString = "SELECT column_name, data_type " + "FROM information_schema.columns "
					+ "WHERE table_schema = :schemaName AND table_name = :tableName AND column_name IN (:column)";
			paramsObj.addValue(SCHEMA_NAME, schemaString);
			paramsObj.addValue(TABLE_NAME, tableName);
			paramsObj.addValue("column", columnList);
			return namedParameterJdbcTemplate.queryForRowSet(sqlString, paramsObj);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

	}

}
