package com.tm.querybuilder.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.dto.WhereGroupListDto;
import com.tm.querybuilder.dto.WhereListDto;
import com.tm.querybuilder.enums.LogicalCondition;

public class WhereClause {

	/**
	 * The method build the where condition by iterating the whereGroup data and where list data
	 * 
	 * @param filterData
	 * @param datatypeMap
	 * @return
	 */
	public StringBuilder whereCondition(FilterData filterData, Map<String, Map<String, Object>> datatypeMap) {

		Gson gson = new Gson();
		StringBuilder whereBuilder = new StringBuilder();
		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		List<WhereGroupListDto> whereClauseList = filterData.getWhereData();

		String jsonString = gson.toJson(datatypeMap);
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
