package com.tm.querybuilder.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	public boolean schemaExists(String schemaString) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String schemaExistsSql = "SELECT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = :schemaName)";
		params.addValue(SCHEMA_NAME, schemaString);
		return namedParameterJdbcTemplate.queryForObject(schemaExistsSql, params, Boolean.class);
	}

	// In the schema check whether the table is exist or not.
	@Override
	public boolean checkTablesExistInSchema(String schemaString) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String sql = "SELECT EXISTS (" + "   SELECT 1" + "    FROM information_schema.tables"
				+ "    WHERE table_schema = :schemaName" + ")";
		params.addValue(SCHEMA_NAME, schemaString);
		return namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);

	}

	// In this method it validate the table in the schema
	@Override
	public boolean validateTableExists(String tableString, String schemaString) {

		String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :tableName AND table_schema = :schemaName";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(TABLE_NAME, tableString);
		parameters.addValue(SCHEMA_NAME, schemaString);
		Integer count = namedParameterJdbcTemplate.queryForObject(query, parameters, Integer.class);
		return count != null && count > 0;
	}
	
	//In this method validate the column by using schema and table name using column list
	@Override
	public boolean validateColumnsExist(List<String> columnsList, String tableString, String schemaString) {
		String queryString = "SELECT COUNT(*) FROM information_schema.columns WHERE column_name IN (:columns) AND table_name = :tableName AND table_schema = :schemaName";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("columns", columnsList);
		parameters.addValue(TABLE_NAME, tableString);
		parameters.addValue(SCHEMA_NAME, schemaString);
		Integer count = namedParameterJdbcTemplate.queryForObject(queryString, parameters, Integer.class);
		return count != null && count == columnsList.size();
	}

	// This method will return the column And TableName of the database
	@Override
	public Map<String, Map<String, String>> getTableColumn(String schemaString) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		List<String> tableNames = new ArrayList<>();

		params.addValue(SCHEMA_NAME, schemaString);
		// Query to get all table names in the database
		String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = :schemaName";
		SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sql, params);

		// Iterate over the result set to get table names
		while (rowSet.next()) {
			tableNames.add(rowSet.getString("table_name"));
		}
		// Query to get column names and data types for each table
		sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";

		// Iterate over the table names and execute the query to get column names and
		// data types
		for (String tableName : tableNames) {
			Map<String, String> columnMap = new LinkedHashMap<>();
			rowSet = jdbcTemplate.queryForRowSet(sql, schemaString, tableName);

			// Iterate over the result set to get column names and data types
			while (rowSet.next()) {
				columnMap.put(rowSet.getString(COLUMN_NAME), rowSet.getString(DATA_TYPE));
			}
			// Add the table name and column names and data types to the schema map
			schemaMap.put(tableName, columnMap);
		}
		// Add the table names to the schema map as a separate entry for easy access

		String tableNamesString = String.join(",", tableNames);
		Map<String, String> tableNameMap = new LinkedHashMap<>();
		tableNameMap.put("tableNames", tableNamesString);
		schemaMap.put(TABLE_NAME, tableNameMap);

		return schemaMap;
	}

	
	// This method will get the query in parameter and execute
	@Override
	public Map<String, Object> getQueryExecution(Map<String, String> query) {
		Map<String, Object> responseMap = new HashMap<>();
		Collection<String> collection = query.values();
		String delimiter = ","; // Delimiter to separate the elements
		String sql = String.join(delimiter, collection);
		List<Map<String, Object>> queryResponseList = jdbcTemplate.queryForList(sql);
		responseMap.put("filterResponse", queryResponseList);
		return responseMap;

	}

	// get the data type of the column in where clause
	@Override
	public Map<String, Map<String, String>> getDataType(FilterData filterData) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String schemaString = filterData.getSchemaName();
		String tableString = filterData.getTableName();
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		List<String> columnsList = new ArrayList<>();
		List<WhereGroupListDto> whereClause = filterData.getWhereData();
		for (int whereGroup = 0; whereGroup < whereClause.size(); whereGroup++) {
			List<WhereListDto> whereGroupList = filterData.getWhereData().get(whereGroup).getWhereList();
			for (int whereList = 0; whereList < whereGroupList.size(); whereList++) {
				String column = whereClause.get(whereGroup).getWhereList().get(whereList).getColumn();
				columnsList.add(column);
			}
		}
		String sql = "SELECT column_name, data_type " + "FROM information_schema.columns "
				+ "WHERE table_schema = :schemaName AND table_name = :tableName AND column_name IN (:column)";
		params.addValue(SCHEMA_NAME, schemaString);
		params.addValue(TABLE_NAME, tableString);
		params.addValue("column", columnsList);
		SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sql, params);
		Map<String, String> columnMap = new LinkedHashMap<>();
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
		String tableString = filterData.getTableName();

		Map<String, Map<String, String>> schemaMap = getDataType(filterData);
		String jsonString = gson.toJson(schemaMap);
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		// Find the corresponding table in the JSON
		JsonObject table = jsonObject.getAsJsonObject(tableString);
		for (int whereGroup = 0; whereGroup < whereClauseList.size(); whereGroup++) {
			List<WhereListDto> whereGroupList = filterData.getWhereData().get(whereGroup).getWhereList();
			LogicalCondition logicalConWhereGroup = whereClauseList.get(whereGroup).getLogicalCondition();

			for (int whereList = 0; whereList < whereGroupList.size(); whereList++) {
				String column = whereClauseList.get(whereGroup).getWhereList().get(whereList).getColumn();
				Object value = whereGroupList.get(whereList).getValue();
				String conditionOperator = whereGroupList.get(whereList).getCondition().getOperator();

				LogicalCondition logicalConWhereList = whereGroupList.get(whereList).getLogicalCondition();

				String dataType = table.get(column).getAsString();

				Set<String> operatorInt = new HashSet<>(
						Arrays.asList("int", "float", "tinyint", "bigint", "double", "decimal"));
				Set<String> operatorString = new HashSet<>(Arrays.asList("varchar", "char", "enum", "text"));

				if (operatorInt.contains(dataType)) {
					if (whereList == 0 && whereGroup == 0) {
						whereBuilder.append("(").append(column).append(" ").append(conditionOperator).append(" ")
								.append(value);
					} else if ((whereList > 0 && whereGroup == 0) || whereList > 0 && whereGroup > 0) {
						whereBuilder.append(" ").append(previousGroupList).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" ").append(value);
					} else if (whereList == 0 && whereGroup > 0) {
						whereBuilder.append(" ").append(previousGroup).append("(").append(" ").append(column)
								.append(" ").append(conditionOperator).append(" ").append(value);
					}
				} else if (operatorString.contains(dataType)) {
					if (whereList == 0 && whereGroup == 0) {
						whereBuilder.append("(").append(column).append(" ").append(conditionOperator).append(" '")
								.append(value).append("'");
					} else if ((whereList > 0 && whereGroup == 0) || whereList > 0 && whereGroup > 0) {
						whereBuilder.append(" ").append(previousGroupList).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" '").append(value).append("'");
					} else if (whereList == 0 && whereGroup > 0) {
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
