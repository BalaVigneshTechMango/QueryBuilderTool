package com.query.builder.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.query.builder.common.WhereClause;
import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;
import com.query.builder.dto.WhereGroupListDto;
import com.query.builder.dto.WhereListDto;
import com.query.builder.enums.LogicalCondition;
import com.query.builder.request.BuilderRequestPojo;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public QueryBuilderDaoImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// this method will check schema Name in database
	@Override
	public boolean schemaExists(String schemaName) {
		String schemaExistsSql = "SELECT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = ?)";
		return jdbcTemplate.queryForObject(schemaExistsSql, Boolean.class, schemaName);
	}

	// This method will return the column And TableName of the database
	@Override
	public Map<String, Map<String, String>> getTableColumn(String schemaName) {
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		List<String> tableNames = new ArrayList<>();

		// Query to get all table names in the database
		String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, schemaName);

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
			rowSet = jdbcTemplate.queryForRowSet(sql, schemaName, tableName);

			// Iterate over the result set to get column names and data types
			while (rowSet.next()) {
				columnMap.put(rowSet.getString("column_name"), rowSet.getString("data_type"));
			}
			// Add the table name and column names and data types to the schema map
			schemaMap.put(tableName, columnMap);
		}
		// Add the table names to the schema map as a separate entry for easy access

		String tableNamesString = String.join(",", tableNames);
		Map<String, String> tableNameMap = new LinkedHashMap<>();
		tableNameMap.put("tableNames", tableNamesString);
		schemaMap.put("tableName", tableNameMap);

		return schemaMap;
	}

	// This method will get the query in parameter and execute
	public Map<String, Object> getFilterData(Map<String, String> query) {
		Map<String, Object> response = new HashMap<>();
		Collection<String> collection = query.values();
		String delimiter = ","; // Delimiter to separate the elements
		String sql = String.join(delimiter, collection);
		List<Map<String, Object>> queryResponse = jdbcTemplate.queryForList(sql);
		response.put("filterResponse", queryResponse);
		return response;

	}

	// This method will generate the query based on the request
	@Override
	public Map<String, String> getFilterQuery(FilterData filterData) {

		String schemaName = filterData.getSchemaName();
		Map<String, String> previewQuery = new HashMap<>();// query
		List<WhereGroupListDto> whereCla = filterData.getWhereData();
		List<String> columnNames = filterData.getColumnNames();
		String columnName = String.join(",", columnNames);
		String table = filterData.getTableName();
		
		if (whereCla != null) {
			StringBuilder whereBuilder = whereCondition(filterData);
			String sql = "Select " + columnName + " From " + schemaName + "." + table + " Where " + whereBuilder;
			previewQuery.put("query", sql);
		} else {
			String sql = "SELECT " + columnName + " FROM " + schemaName + "." + table;
			previewQuery.put("query", sql);
		}

		return previewQuery;

	}

	@Override
	public Map<String, Map<String, String>> dataTypeColumn(String tableName, String schemaName) {
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";
		Map<String, String> columnMap = new LinkedHashMap<>();
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, schemaName, tableName);

		// Iterate over the result set to get column names and data types
		while (rowSet.next()) {
			columnMap.put(rowSet.getString("column_name"), rowSet.getString("data_type"));
		}
		schemaMap.put(tableName, columnMap);

		return schemaMap;

	}

	public StringBuilder whereCondition(FilterData filterData) {
		Gson gson = new Gson();
		StringBuilder whereBuilder = new StringBuilder();
		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		List<WhereGroupListDto> whereClause = filterData.getWhereData();
		String tableName = filterData.getTableName();
		String schemaName = filterData.getSchemaName();
		whereBuilder.append("(");
		for (int whereGroup = 0; whereGroup < whereClause.size(); whereGroup++) {
			List<WhereListDto> whereGroupList = filterData.getWhereData().get(whereGroup).getWhereList();
			LogicalCondition logicalConWhereGroup = whereClause.get(whereGroup).getLogicalCondition();

			for (int whereList = 0; whereList < whereGroupList.size(); whereList++) {
				String column = whereClause.get(whereGroup).getWhereList().get(whereList).getColumn();
				Object value = whereGroupList.get(whereList).getValue();
				String conditionOperator = whereGroupList.get(whereList).getCondition().getOperator();
				LogicalCondition logicalConWhereList = whereGroupList.get(whereList).getLogicalCondition();

				Map<String, Map<String, String>> schemaMap = dataTypeColumn(tableName, schemaName);
				String jsonString = gson.toJson(schemaMap);
				JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
				// Find the corresponding table in the JSON
				JsonObject table = jsonObject.getAsJsonObject(tableName);
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
		whereBuilder.append(")");
		return whereBuilder;
	}

	@Override
	public Map<String, String> getJoinQuery(BuilderRequestPojo builderRequestPojo) {
		Map<String, String> response = new HashMap<>();
		LogicalCondition previousValue = null; // Variable to store the previous value of Group
		StringBuilder queryBuilder = new StringBuilder();
		List<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
		List<String> columnNames = joinDatas.get(0).getColumnNames();

		// queryBuilder.setLength(queryBuilder.length() - 2);
		for (int index = 0; index < joinDatas.size(); index++) {
			String leftTableName = joinDatas.get(index).getLsTableName();
			String rightTableName = joinDatas.get(index).getRsTableName();
			String joinType = joinDatas.get(index).getJoinType().getOperator();
			int joinConditionSize = joinDatas.get(index).getJoinCondition().size();
			for (int condition = 0; condition < joinConditionSize; condition++) {
				LogicalCondition logicalCondition = joinDatas.get(index).getJoinCondition().get(condition)
						.getLogicalCondition();
				String leftJoinColumn = joinDatas.get(index).getJoinCondition().get(condition).getLsColumn();
				String rightJoinColumn = joinDatas.get(index).getJoinCondition().get(condition).getRsColumn();
				String conditionType = joinDatas.get(index).getJoinCondition().get(condition).getCondition()
						.getOperator();
				if (index == 0 && condition == 0) {
					queryBuilder.append(" FROM ").append(leftTableName);
					queryBuilder.append(" " + joinType + " ").append(rightTableName).append(" ON ")
							.append(leftTableName).append(".").append(leftJoinColumn).append(" " + conditionType + " ")
							.append(rightTableName).append(".").append(rightJoinColumn);
				} else if (index > 0) {
					queryBuilder.append(" " + joinType + " ").append(leftTableName).append(" ON ").append(leftTableName)
							.append(".").append(leftJoinColumn).append(" " + conditionType + " ").append(rightTableName)
							.append(".").append(rightJoinColumn);
				} else if (condition > 0) {
					queryBuilder.append(" " + previousValue + " ").append(leftTableName).append(".")
							.append(leftJoinColumn).append(" " + conditionType + " ").append(rightTableName).append(".")
							.append(rightJoinColumn);
				}
				previousValue = logicalCondition;
			}
		}
		String columnName = String.join(",", columnNames);

		// String sql="Select " + columnName + " From " + dataBaseName+"."+table;
		List<WhereGroupListDto> whereCla = joinDatas.get(0).getWhereData();
		if (whereCla != null) {
			queryBuilder.append(" WHERE ");
			WhereClause whereClause = new WhereClause();
			StringBuilder whereBuilder = whereClause.whereConditions(builderRequestPojo, 0);
			response.put("query", queryBuilder.toString() + whereBuilder);

		} else {
			response.put("query", queryBuilder.toString());

		}
		return response;

	}

	// This Api for dynamic join query for multiple tables
	public Map<String, Object> getJoinedData(Map<String, String> query) {
		Map<String, Object> response = new HashMap<>();
		Collection<String> collection = query.values();
		String delimiter = ","; // Delimiter to separate the elements
		String sql = String.join(delimiter, collection);
		List<Map<String, Object>> joinData = jdbcTemplate.queryForList(sql);
		response.put("JoinResponse", joinData);
		return response;
	}
}
