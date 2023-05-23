package com.query.builder.dao.impl;

import java.util.ArrayList;
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

		return schemaMap;
	}

	// 5. This Api is filter condition of the selected columns for the tables
	public Map<String, Object> getFilterData(FilterData filterData) {
		WhereClause whereClause = new WhereClause();
		Map<String, Object> response = new HashMap<>();
		List<WhereGroupListDto> whereCondition = filterData.getWhereGroupList();
		List<String> columnNames = filterData.getColumnNames();
		String columnName = String.join(",", columnNames);
		String table = filterData.getTableName();
		if (whereCondition != null) {
			StringBuilder whereBuilder = whereClause.whereCondition(filterData);
			String sql = "SELECT " + columnName + " From " + table + " Where " + whereBuilder;
			List<Map<String, Object>> queryResponse = jdbcTemplate.queryForList(sql);
			response.put("filterResponse", queryResponse);
		} else {
			String sql = "Select " + columnName + " From " + table;
			List<Map<String, Object>> queryResponse = jdbcTemplate.queryForList(sql);
			response.put("filterResponse", queryResponse);
			
		}
		return response;

	}

	@Override
	public Map<String, String> getFilterQuery(FilterData filterData) {
		WhereClause whereClause = new WhereClause();
		Map<String, String> previewQuery = new HashMap<>();// query
		List<WhereGroupListDto> whereCla = filterData.getWhereGroupList();
		List<String> columnNames = filterData.getColumnNames();
		String columnName = String.join(",", columnNames);
		String table = filterData.getTableName();
		if (whereCla != null) {
			StringBuilder whereBuilder = whereClause.whereCondition(filterData);
			String sql = "Select " + columnName + " From " + table + " Where " + whereBuilder;
			previewQuery.put("query", sql);
		} else {
			String sql = "SELECT " + columnName + " FROM " + table;
			previewQuery.put("query", sql);
		}
		return previewQuery;

	}

	// 4. select * from L1 inner join R1 on ((LT1 == RT1) and (Lt2 != RT2))
	// This Api for dynamic join query for multiple tables
	public List<Map<String, Object>> getJoinedData(BuilderRequestPojo builderRequestPojo) {
		LogicalCondition previousValue = null; // Variable to store the previous value of Group
		StringBuilder queryBuilder = new StringBuilder();
		List<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
		List<String> columnNames = joinDatas.get(0).getColumnNames();
		queryBuilder.append("Select ");
		for (String column : columnNames) {
			queryBuilder.append(column).append(", ");
		}
		queryBuilder.setLength(queryBuilder.length() - 2);
		for (int index = 0; index < joinDatas.size(); index++) {
			String leftTableName = joinDatas.get(index).getLsTableName();
			String rightTableName = joinDatas.get(index).getRsTableName();
			String joinType = joinDatas.get(index).getJoinsTypes().getOperator();
			int joinConditionSize = joinDatas.get(index).getJoinCondition().size();
			for (int condition = 0; condition < joinConditionSize; condition++) {
				LogicalCondition logicalCondition = joinDatas.get(index).getJoinCondition().get(condition)
						.getLogicalCondition();
				String leftJoinColumn = joinDatas.get(index).getJoinCondition().get(condition).getLsColumn();
				String rightJoinColumn = joinDatas.get(index).getJoinCondition().get(condition).getRsColumn();
				String conditionType = joinDatas.get(index).getJoinCondition().get(condition).getCondition()
						.getOperator();
				if (index == 0 && condition == 0) {
					queryBuilder.append(" From ").append(leftTableName);
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
		List<WhereGroupListDto> whereCla = joinDatas.get(0).getWhereGroupList();
		if (whereCla != null) {
			queryBuilder.append(" Where ");
			WhereClause whereClause = new WhereClause();
			StringBuilder whereBuilder = whereClause.whereConditions(builderRequestPojo, 0);
			return jdbcTemplate.queryForList(queryBuilder.toString() + whereBuilder);
		} else {
			return jdbcTemplate.queryForList(queryBuilder.toString());
		}

	}

	@Override
	public List<Map<String, Object>> getJoinQuery(BuilderRequestPojo builderRequestPojo) {
		LogicalCondition previousValue = null; // Variable to store the previous value of Group
		StringBuilder queryBuilder = new StringBuilder();
		List<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
		List<String> columnNames = joinDatas.get(0).getColumnNames();
		queryBuilder.append("SELECT ");
		for (String column : columnNames) {
			queryBuilder.append(column).append(", ");
		}
		queryBuilder.setLength(queryBuilder.length() - 2);
		for (int index = 0; index < joinDatas.size(); index++) {
			String leftTableName = joinDatas.get(index).getLsTableName();
			String rightTableName = joinDatas.get(index).getRsTableName();
			String joinType = joinDatas.get(index).getJoinsTypes().getOperator();
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
		List<WhereGroupListDto> whereCla = joinDatas.get(0).getWhereGroupList();
		if (whereCla != null) {
			queryBuilder.append(" WHERE ");
			WhereClause whereClause = new WhereClause();
			StringBuilder whereBuilder = whereClause.whereConditions(builderRequestPojo, 0);
			List<Map<String, Object>> previewQuery = new ArrayList<>();// query
			Map<String, Object> query = new HashMap<>();
			query.put("Joindata", queryBuilder.toString() + whereBuilder);
			previewQuery.add(query);
			return previewQuery;
		} else {
			List<Map<String, Object>> previewQuery = new ArrayList<>();// query
			Map<String, Object> query = new HashMap<>();
			query.put("Joindata", queryBuilder.toString());
			previewQuery.add(query);
			return previewQuery;
		}

	}

}
