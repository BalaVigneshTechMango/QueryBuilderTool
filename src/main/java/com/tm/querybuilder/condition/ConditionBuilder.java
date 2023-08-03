package com.tm.querybuilder.condition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tm.querybuilder.enums.Condition;
import com.tm.querybuilder.pojo.ConditionGroupPOJO;
import com.tm.querybuilder.pojo.ConditionPOJO;
import com.tm.querybuilder.pojo.ValuesPOJO;

public class ConditionBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditionBuilder.class);

	/**
	 * The method build the where condition by iterating the whereGroup data and
	 * where list data
	 * 
	 * @param filterData
	 * @param datatypeMap
	 * @return
	 */
	public String fetchCondition(List<ConditionGroupPOJO> conditionGroupList, Map<String, Object> columnDataTypeMap) {
		LOGGER.info("building where condition method");
		StringBuilder conditionBuilder = new StringBuilder();
		try {
			Set<String> operatorString = new HashSet<>(
					Arrays.asList("varchar", "char", "enum", "text", "date", "time", "timestamp", "year"));
			for (ConditionGroupPOJO conditionGroup : conditionGroupList) {
				conditionBuilder.append("(");
				for (ConditionPOJO conditionList : conditionGroup.getConditionList()) {
					conditionBuilder.append(conditionList.getColumn()).append(" ")
							.append(conditionList.getCondition().getOperator());
					if (operatorString.contains(columnDataTypeMap.get(conditionList.getColumn()))
							&& Condition.BETWEEN.equals(conditionList.getCondition())) {
						ObjectMapper mapper = new ObjectMapper();
						ValuesPOJO value = mapper.readValue(mapper.writeValueAsString(conditionList.getValue()),
								ValuesPOJO.class);
						conditionBuilder.append(" '").append(value.getFrom()).append("' ").append("AND ").append("'")
								.append(value.getTo()).append("'");
					} else if (operatorString.contains(columnDataTypeMap.get(conditionList.getColumn()))
							&& Condition.IN.equals(conditionList.getCondition())) {
						List<String> list = (List<String>) conditionList.getValue();
						String value = list.stream().collect(Collectors.joining("','", "'", "'"));
						conditionBuilder.append(" (").append(value).append(")");
					}
					// check whether the column data type is a part of operater list to add single
					// quotes in prefix and suffix
					else if (operatorString.contains(columnDataTypeMap.get(conditionList.getColumn()))) {
						conditionBuilder.append("'").append(conditionList.getValue()).append("'");
					} else {
						conditionBuilder.append(conditionList.getValue());
					}
					// Append condition to the where group list if the condition has value
					// ConditionBuilder will be null if it is the last item of the list.
					if (conditionList.getLogicalCondition() != null) {
						conditionBuilder.append(" ").append(conditionList.getLogicalCondition()).append(" ");
					}
				}
				conditionBuilder.append(")");
				// Append condition to the where list if the condition has value
				// ConditionBuilder will be null if it is the last item of the list
				if (conditionGroup.getLogicalCondition() != null) {
					conditionBuilder.append(" ").append(conditionGroup.getLogicalCondition().name()).append(" ");
				}
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred while building where condition. ");
			throw new DataAccessResourceFailureException("An error occurred while building where condition .",
					exception);
		}
		LOGGER.debug("where ConditionBuilder:{}", conditionBuilder);
		return conditionBuilder.toString();
	}
}
