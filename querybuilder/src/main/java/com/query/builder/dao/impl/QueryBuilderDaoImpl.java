package com.query.builder.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import com.query.builder.common.WhereClause;
import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;
import com.query.builder.dto.WhereGroupListDto;
import com.query.builder.enums.LogicalCondition;
import com.query.builder.request.BuilderRequestPojo;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public QueryBuilderDaoImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private String dataBaseName;

	// 3 This method will return the column And TableName of the database
	@Override
	public Map<String, Map<String, String>> getTableColumn(String dataBase) {
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		List<String> tableNames = new ArrayList<>();

		// Query to get all table names in the database
		String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, dataBase);

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
			rowSet = jdbcTemplate.queryForRowSet(sql, dataBase, tableName);

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

		dataBaseName = dataBase;
		return schemaMap;
	}

	// 5. This Api is filter condition of the selected columns for the tables
	public Map<String, Object> getFilterData(Map<String, String> query) {

		Map<String, Object> response = new HashMap<>();
		Collection<String> collection = query.values();
		String delimiter = ","; // Delimiter to separate the elements
		String sql = String.join(delimiter, collection);
		List<Map<String, Object>> queryResponse = jdbcTemplate.queryForList(sql);
		response.put("filterResponse", queryResponse);
		return response;

	}

	@Override
	public Map<String, String> getFilterQuery(FilterData filterData) {
		WhereClause whereClause = new WhereClause();
		Map<String, String> previewQuery = new HashMap<>();// query
		List<WhereGroupListDto> whereCla = filterData.getWhereData();
		List<String> columnNames = filterData.getColumnNames();
		String columnName = String.join(",", columnNames);
		String table = filterData.getTableName();
		if (whereCla != null) {
			StringBuilder whereBuilder = whereClause.whereCondition(filterData);
			String sql = "Select " + columnName + " From " + dataBaseName + "." + table + " Where " + whereBuilder;
			previewQuery.put("query", sql);
		} else {
			String sql = "SELECT " + columnName + " FROM " + dataBaseName + "." + table;
			previewQuery.put("query", sql);
		}

		return previewQuery;

	}

	// 4. select * from L1 inner join R1 on ((LT1 == RT1) and (Lt2 != RT2))
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

}
