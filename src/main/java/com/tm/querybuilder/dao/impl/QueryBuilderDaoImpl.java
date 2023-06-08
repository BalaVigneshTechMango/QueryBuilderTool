package com.tm.querybuilder.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.dto.WhereGroupListDto;
import com.tm.querybuilder.dto.WhereListDto;
import com.tm.querybuilder.enums.LogicalCondition;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String SCHEMA_NAME = "schemaName";
	private static final String COLUMN_NAME = "column_name";
	private static final String DATA_TYPE = "data_type";
	private static final String TABLE_NAME = "tableName";

	// this method will check schema Name in database
	@Override
	public boolean schemaExistDetails(String schemaString) {
		MapSqlParameterSource paramsObj = new MapSqlParameterSource();
		// Build a query and store in string.
		String existsSqlString = "SELECT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = :schemaName)";
		paramsObj.addValue(SCHEMA_NAME, schemaString);
		return namedParameterJdbcTemplate.queryForObject(existsSqlString, paramsObj, Boolean.class);
	}

	// In the schema check whether the table is exist or not.
	@Override
	public boolean tablesInSchema(String schemaString) {
		MapSqlParameterSource paramsObj = new MapSqlParameterSource();
		// Build a query and store in string.
		String sqlString = "SELECT EXISTS (" + "   SELECT 1" + "    FROM information_schema.tables"
				+ "WHERE table_schema = :schemaName )";
		paramsObj.addValue(SCHEMA_NAME, schemaString);
		return namedParameterJdbcTemplate.queryForObject(sqlString, paramsObj, Boolean.class);

	}

	// In this method it validate the table in the schema
	@Override
	public boolean validateTable(String tableString, String schemaString) {
		// Build a query and store in string.
		String queryString = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :tableName AND table_schema = :schemaName";
		MapSqlParameterSource parametersObj = new MapSqlParameterSource();
		parametersObj.addValue(TABLE_NAME, tableString);
		parametersObj.addValue(SCHEMA_NAME, schemaString);
		Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
		return countInt != null && countInt > 0;
	}

	// In this method validate the column by using schema and table name using
	// column list
	@Override
	public boolean validateColumns(List<String> columnsList, String tableString, String schemaString) {
		// Build a query and store in string.
		String queryString = "SELECT COUNT(*) FROM information_schema.columns WHERE column_name IN (:columns) AND table_name = :tableName AND table_schema = :schemaName";
		MapSqlParameterSource parametersObj = new MapSqlParameterSource();
		parametersObj.addValue("columns", columnsList);
		parametersObj.addValue(TABLE_NAME, tableString);
		parametersObj.addValue(SCHEMA_NAME, schemaString);
		Integer countInt = namedParameterJdbcTemplate.queryForObject(queryString, parametersObj, Integer.class);
		// if the count not equal to null and colunt the column size and if the both
		// condition is okay the returns
		return countInt != null && countInt == columnsList.size();
	}

	// This method will return the column And TableName of the database
	@Override
	public Map<String, Map<String, String>> fetchColumnDetails(String schemaString) {
		MapSqlParameterSource paramsObj = new MapSqlParameterSource();
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		List<String> tableList = new ArrayList<>();

		paramsObj.addValue(SCHEMA_NAME, schemaString);
		// Query to get all table names in the database
		String sqlString = "SELECT table_name FROM information_schema.tables WHERE table_schema = :schemaName";
		SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sqlString, paramsObj);

		// Iterate over the result set to get table names
		while (rowSet.next()) {
			tableList.add(rowSet.getString("table_name"));
		}
		// Query to get column names and data types for each table
		sqlString = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";

		// Iterate over the table names and execute the query to get column names and
		// data types
		for (String tableString : tableList) {
			Map<String, String> columnMap = new LinkedHashMap<>();
			rowSet = jdbcTemplate.queryForRowSet(sqlString, paramsObj, tableString);

			// Iterate over the result set to get column names and data types
			while (rowSet.next()) {
				columnMap.put(rowSet.getString(COLUMN_NAME), rowSet.getString(DATA_TYPE));
			}
			// Add the table name and column names and data types to the schema map
			schemaMap.put(tableString, columnMap);
		}
		// Add the table names to the schema map as a separate entry for easy access

		Map<String, String> tableNameMap = new LinkedHashMap<>();
		tableNameMap.put("tableNames", String.join(",", tableList));
		schemaMap.put(TABLE_NAME, tableNameMap);

		return schemaMap;
	}

	// This method will get the query in parameter and execute
	@Override
	public Map<String, Object> fetchResultData(String queryString) {
		Map<String, Object> responseMap = new HashMap<>();
		List<Map<String, Object>> queryResponseMap = jdbcTemplate.queryForList(queryString);
		responseMap.put("filterResponse", queryResponseMap);
		return responseMap;

	}

	// get the data type of the column in where clause
	@Override
	public Map<String, Map<String, String>> getDataType(FilterData filterData) {
		MapSqlParameterSource paramsObj = new MapSqlParameterSource();
		String tableString = filterData.getTableName();
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		List<String> columnsList = new ArrayList<>();
		List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
		for (int whereGroup = 0; whereGroup < whereClauseList.size(); whereGroup++) {
			for (int whereList = 0; whereList < filterData.getWhereData().get(whereGroup).getWhereList()
					.size(); whereList++) {
				columnsList.add(whereClauseList.get(whereGroup).getWhereList().get(whereList).getColumn());
			}
		}
		// Build a query and store in string
		String sqlString = "SELECT column_name, data_type " + "FROM information_schema.columns "
				+ "WHERE table_schema = :schemaName AND table_name = :tableName AND column_name IN (:column)";
		paramsObj.addValue(SCHEMA_NAME, filterData.getSchemaName());
		paramsObj.addValue(TABLE_NAME, tableString);
		paramsObj.addValue("column", columnsList);
		SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sqlString, paramsObj);
		Map<String, String> columnMap = new LinkedHashMap<>();
		// while will get the column and datatype of the from rowset and put in
		// columnMap
		while (rowSet.next()) {
			columnMap.put(rowSet.getString(COLUMN_NAME), rowSet.getString(DATA_TYPE));
		}
		schemaMap.put(tableString, columnMap);
		return schemaMap;
	}

	// This method will build the entire where group and where list.
	@Override
	public StringBuilder whereCondition(FilterData filterData) {
		Gson gson = new Gson();
		StringBuilder whereBuilder = new StringBuilder();
		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
		Map<String, Map<String, String>> schemaMap = getDataType(filterData);
		String jsonString = gson.toJson(schemaMap);
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		// Find the corresponding table in the JSON
		JsonObject table = jsonObject.getAsJsonObject(filterData.getTableName());
		// this whereData list will be iterated in this loop by getting where clause
		// size
		for (int whereGroupInt = 0; whereGroupInt < whereClauseList.size(); whereGroupInt++) {
			List<WhereListDto> whereGroupList = filterData.getWhereData().get(whereGroupInt).getWhereList();
			LogicalCondition logicalConWhereGroup = whereClauseList.get(whereGroupInt).getLogicalCondition();

			for (int whereList = 0; whereList < whereGroupList.size(); whereList++) {
				String column = whereClauseList.get(whereGroupInt).getWhereList().get(whereList).getColumn();
				Object value = whereGroupList.get(whereList).getValue();
				String conditionOperator = whereGroupList.get(whereList).getCondition().getOperator();

				LogicalCondition logicalConWhereList = whereGroupList.get(whereList).getLogicalCondition();
				// get the datatype of column in wherelist one by one
				String dataType = table.get(column).getAsString();

				Set<String> operatorInt = new HashSet<>(
						Arrays.asList("int", "float", "tinyint", "bigint", "double", "decimal"));
				Set<String> operatorString = new HashSet<>(Arrays.asList("varchar", "char", "enum", "text"));
				// this if/else will check the datatype by using contains in hashset
				if (operatorInt.contains(dataType)) {
					// this if/else if will build query as per index in the loop
					if (whereList == 0 && whereGroupInt == 0) {
						whereBuilder.append("(").append(column).append(" ").append(conditionOperator).append(" ")
								.append(value);
					} else if ((whereList > 0 && whereGroupInt == 0) || whereList > 0 && whereGroupInt > 0) {
						whereBuilder.append(" ").append(previousGroupList).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" ").append(value);
					} else if (whereList == 0 && whereGroupInt > 0) {
						whereBuilder.append(" ").append(previousGroup).append("(").append(" ").append(column)
								.append(" ").append(conditionOperator).append(" ").append(value);
					}
				} else if (operatorString.contains(dataType)) {
					// this if/else if will build query as per index in the loop
					if (whereList == 0 && whereGroupInt == 0) {
						whereBuilder.append("(").append(column).append(" ").append(conditionOperator).append(" '")
								.append(value).append("'");
					} else if ((whereList > 0 && whereGroupInt == 0) || whereList > 0 && whereGroupInt > 0) {
						whereBuilder.append(" ").append(previousGroupList).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" '").append(value).append("'");
					} else if (whereList == 0 && whereGroupInt > 0) {
						whereBuilder.append(" ").append(previousGroup).append("(").append(" ").append(column)
								.append(" ").append(conditionOperator).append(" '").append(value).append("'");
					}
				}
				previousGroupList = logicalConWhereList;
				previousGroup = logicalConWhereGroup;
			}
			whereBuilder.append(")");
		}
		return whereBuilder;
	}

}
