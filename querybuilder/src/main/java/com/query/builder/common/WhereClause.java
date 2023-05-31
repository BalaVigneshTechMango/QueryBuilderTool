package com.query.builder.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.dao.impl.QueryBuilderDaoImpl;
import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;
import com.query.builder.dto.WhereGroupListDto;
import com.query.builder.dto.WhereListDto;
import com.query.builder.enums.LogicalCondition;
import com.query.builder.request.BuilderRequestPojo;

@Repository
public class WhereClause {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public StringBuilder whereCondition(FilterData filterData, String dataBase, JdbcTemplate jdbcTemplate) {

		QueryBuilderDao queryBuilderDao = new QueryBuilderDaoImpl(dataSource);
		Gson gson = new Gson();
		StringBuilder whereBuilder = new StringBuilder();

		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		List<WhereGroupListDto> whereClause = filterData.getWhereData();
		String tableName = filterData.getTableName();

		for (int whereGroup = 0; whereGroup < whereClause.size(); whereGroup++) {
			List<WhereListDto> whereGroupList = filterData.getWhereData().get(whereGroup).getWhereList();
			for (int whereList = 0; whereList < whereGroupList.size(); whereList++) {
				String column = whereClause.get(whereGroup).getWhereList().get(whereList).getColumn();
				Object value = whereGroupList.get(whereList).getValue();
				String conditionOperator = whereGroupList.get(whereList).getCondition().getOperator();
				LogicalCondition logicalConWhereList = whereGroupList.get(whereList).getLogicalCondition();
				LogicalCondition logicalConWhereGroup = whereClause.get(whereGroup).getLogicalCondition();

				Map<String, Map<String, String>> schemaMap = queryBuilderDao.dataTypeColumn(tableName, dataBase);

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
						whereBuilder.append(column).append(" ").append(conditionOperator).append(" ").append(value);
					} else if ((whereList > 0 && whereGroup == 0) || whereList > 0 && whereGroup > 0) {
						whereBuilder.append(" ").append(previousGroupList).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" ").append(value);
					} else if (whereList == 0 && whereGroup > 0) {
						whereBuilder.append(" ").append(previousGroup).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" ").append(value);
					}
				} else if (operatorString.contains(dataType)) {
					if (whereList == 0 && whereGroup == 0) {
						whereBuilder.append(column).append(" ").append(conditionOperator).append(" '").append(value)
								.append("'");
					} else if ((whereList > 0 && whereGroup == 0) || whereList > 0 && whereGroup > 0) {
						whereBuilder.append(" ").append(previousGroupList).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" '").append(value).append("'");
					} else if (whereList == 0 && whereGroup > 0) {
						whereBuilder.append(" ").append(previousGroup).append(" ").append(column).append(" ")
								.append(conditionOperator).append(" '").append(value).append("'");
					}
				}
				previousGroupList = logicalConWhereList;
				previousGroup = logicalConWhereGroup;
			}
		}
		return whereBuilder;
	}

	public StringBuilder whereConditions(BuilderRequestPojo builderRequestPojo, int index) {
		List<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.setLength(0);
		List<WhereGroupListDto> whereClause = joinDatas.get(index).getWhereData();
		for (int whereGroup = 0; whereGroup < whereClause.size(); whereGroup++) {
			List<WhereListDto> whereGroupList = joinDatas.get(index).getWhereData().get(whereGroup).getWhereList();
			for (int whereList = 0; whereList < whereGroupList.size(); whereList++) {
				String column = whereClause.get(whereGroup).getWhereList().get(whereList).getColumn();
				Object value = whereGroupList.get(whereList).getValue();
				String conditionOperator = whereGroupList.get(whereList).getCondition().getOperator();
				LogicalCondition logicalConWhereList = whereGroupList.get(whereList).getLogicalCondition();
				LogicalCondition logicalConWhereGroup = whereClause.get(whereGroup).getLogicalCondition();
				if (whereList == 0 && whereGroup == 0) {
					whereBuilder.append(column).append(" ").append(conditionOperator).append(" '").append(value)
							.append("'");
				} else if ((whereList > 0 && whereGroup == 0) || whereList > 0 && whereGroup > 0) {
					whereBuilder.append(" ").append(previousGroupList).append(" ").append(column).append(" ")
							.append(conditionOperator).append(" '").append(value).append("'");
				} else if (whereList == 0 && whereGroup > 0) {
					whereBuilder.append(" ").append(previousGroup).append(" ").append(column).append(" ")
							.append(conditionOperator).append(" '").append(value).append("'");
				}
				previousGroupList = logicalConWhereList;
				previousGroup = logicalConWhereGroup;
			}
		}
		return whereBuilder;
	}

	private Map<String, Map<String, String>> dataType(String tableName, String dataBase) {
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();

		String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";

		Map<String, String> columnMap = new LinkedHashMap<>();
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, dataBase, tableName);

		// Iterate over the result set to get column names and data types
		while (rowSet.next()) {
			columnMap.put(rowSet.getString("column_name"), rowSet.getString("data_type"));
		}
		schemaMap.put(tableName, columnMap);

		return schemaMap;

	}

}
