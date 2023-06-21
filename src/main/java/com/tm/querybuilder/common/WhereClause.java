package com.tm.querybuilder.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.dto.WhereGroupListDto;
import com.tm.querybuilder.dto.WhereListDto;

public class WhereClause {

	/**
	 * The method build the where condition by iterating the whereGroup data and
	 * where list data
	 * 
	 * @param filterData
	 * @param datatypeMap
	 * @return
	 */
	public String whereCondition(FilterData filterData, Map<String, Map<String, Object>> datatypeMap) {
		Map<String, Object> columnDataTypeMap = datatypeMap.get(filterData.getTableName());
		Set<String> operatorString = new HashSet<>(Arrays.asList("varchar", "char", "enum", "text"));
		StringBuilder whereBuilder = new StringBuilder();
		List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
		for (WhereGroupListDto whereGroupListDto : whereClauseList) {
			List<WhereListDto> whereList = whereGroupListDto.getWhereList();
			StringBuilder whereGroupBuilder = new StringBuilder();
			for (WhereListDto whereListDto : whereList) {
				whereGroupBuilder.append(whereListDto.getColumn()).append(whereListDto.getCondition().getOperator());
				// check whether the column data type is a part of operater list to add single
				// quotes in prefix and suffix
				if (operatorString.contains(columnDataTypeMap.get(whereListDto.getColumn()))) {
					whereGroupBuilder.append("'").append(whereListDto.getValue()).append("'");
				} else {
					whereGroupBuilder.append(whereListDto.getValue());
				}
				// Append condition to the where group list if the condition has value
				// Condition will be null if it is the last item of the list
				if (whereListDto.getLogicalCondition() != null) {
					whereGroupBuilder.append(" ").append(whereListDto.getLogicalCondition()).append(" ");
				}
			}
			// Start and close with paranthesis if inner condition list has value
			// Append the where group string to the where list
			if (!whereGroupBuilder.isEmpty()) {
				whereBuilder.append("(").append(whereGroupBuilder.toString()).append(")");
			}
			// Append condition to the where list if the condition has value
			// Condition will be null if it is the last item of the list
			if (whereGroupListDto.getLogicalCondition() != null) {
				whereBuilder.append(" ").append(whereGroupListDto.getLogicalCondition().name()).append(" ");
			}
		}
		return whereBuilder.toString();
	}

}
