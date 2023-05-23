package com.query.builder.common;

import java.util.List;

import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;
import com.query.builder.dto.WhereGroupListDto;
import com.query.builder.dto.WhereListDto;
import com.query.builder.enums.LogicalCondition;
import com.query.builder.request.BuilderRequestPojo;

public class WhereClause {

	public StringBuilder whereCondition(FilterData filterData) {

		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		StringBuilder whereBuilder = new StringBuilder();
		List<WhereGroupListDto> whereClause = filterData.getWhereGroupList();
		for (int whereGroup = 0; whereGroup < whereClause.size(); whereGroup++) {
			List<WhereListDto> whereGroupList = filterData.getWhereGroupList().get(whereGroup).getWhereList();
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

	public StringBuilder whereConditions(BuilderRequestPojo builderRequestPojo, int index) {
		List<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.setLength(0);
		List<WhereGroupListDto> whereClause = joinDatas.get(index).getWhereGroupList();
		for (int whereGroup = 0; whereGroup < whereClause.size(); whereGroup++) {
			List<WhereListDto> whereGroupList = joinDatas.get(index).getWhereGroupList().get(whereGroup).getWhereList();
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

}
