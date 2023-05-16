package com.query.builder.common;

import java.util.List;

import com.query.builder.dto.FilterDataPojo;
import com.query.builder.dto.WhereGroupListDto;
import com.query.builder.dto.WhereListDto;
import com.query.builder.enums.LogicalCondition;
import com.query.builder.request.BuilderRequestPojo;

public class WhereClause {

	public StringBuilder whereCondition(BuilderRequestPojo builderRequestPojo, int index) {
		List<FilterDataPojo> filterPojos = builderRequestPojo.getFilterData();
		LogicalCondition previousGroupList = null; // Variable to store the previous value of groupList
		LogicalCondition previousGroup = null; // Variable to store the previous value of Group
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.setLength(0);
		List<WhereGroupListDto> whereCla = filterPojos.get(index).getWhereGroupList();
		for (int whereGroup = 0; whereGroup < whereCla.size(); whereGroup++) {
			List<WhereListDto> whereCondition = filterPojos.get(index).getWhereGroupList().get(whereGroup)
					.getWhereList();
			for (int whereList = 0; whereList < whereCondition.size(); whereList++) {
				String column = filterPojos.get(index).getWhereGroupList().get(whereGroup).getWhereList().get(whereList)
						.getColumn();
				Object value = filterPojos.get(index).getWhereGroupList().get(whereGroup).getWhereList().get(whereList)
						.getValue();
				String conditionOperator = filterPojos.get(index).getWhereGroupList().get(whereGroup).getWhereList()
						.get(whereList).getCondition().getOperator();

				LogicalCondition logicalConWhereList = filterPojos.get(index).getWhereGroupList().get(whereGroup)
						.getWhereList().get(whereList).getLogicalCondition();

				LogicalCondition logicalConWhereGroup = filterPojos.get(index).getWhereGroupList().get(whereGroup)
						.getLogicalCondition();
				if (whereList == 0 && whereGroup == 0) {
					whereBuilder.append(" " + column + " " + " " + conditionOperator + " " + "'" + value + "'" + " ");
				}
				if (whereList > 0 && whereGroup == 0) {
					whereBuilder.append(" " + previousGroupList + " " + " " + column + " " + " " + " "
							+ conditionOperator + " " + "'" + value + "'" + " ");
				}
				if (whereList == 0 && whereGroup > 0) {
					whereBuilder.append(" " + previousGroup + " " + column + " " + conditionOperator + " " + "'" + value
							+ "'" + " ");
				}
				if (whereList > 0 && whereGroup > 0) {
					whereBuilder.append(" " + previousGroupList + " " + column + " " + " " + conditionOperator + " "
							+ " " + "'" + value + "'" + " ");
				}
				previousGroupList = logicalConWhereList;
				previousGroup = logicalConWhereGroup;
			}
		}
		return whereBuilder;
	}

}
