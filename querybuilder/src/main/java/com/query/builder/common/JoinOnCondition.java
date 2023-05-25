package com.query.builder.common;

import java.util.List;

import com.query.builder.dto.JoinData;
import com.query.builder.enums.LogicalCondition;
import com.query.builder.request.BuilderRequestPojo;

public class JoinOnCondition {

	public StringBuilder onCondition(BuilderRequestPojo builderRequestPojo) {
		StringBuilder queryBuilder=new StringBuilder();
		LogicalCondition previousValue = null;
		
		  List<JoinData>joinDatas= builderRequestPojo.getRequestData().getJoinDatas();
		
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
		return queryBuilder;
	}
	
}
