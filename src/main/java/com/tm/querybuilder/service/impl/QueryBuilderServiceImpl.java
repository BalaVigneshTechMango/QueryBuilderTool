package com.tm.querybuilder.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tm.querybuilder.constant.QueryConstants;
import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.ColumnDatatypeDTO;
import com.tm.querybuilder.dto.ColumnDetailsDTO;
import com.tm.querybuilder.enums.Condition;
import com.tm.querybuilder.pojo.FilterDataPOJO;
import com.tm.querybuilder.pojo.JoinConditionPOJO;
import com.tm.querybuilder.pojo.JoinDataPOJO;
import com.tm.querybuilder.pojo.OrderByPOJO;
import com.tm.querybuilder.pojo.ValuesPOJO;
import com.tm.querybuilder.pojo.WhereGroupListPOJO;
import com.tm.querybuilder.pojo.WhereListPOJO;
import com.tm.querybuilder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryBuilderServiceImpl.class);

	/**
	 * @param schemaName
	 * @return get table and columm by using schema name get the details with dao
	 *         layer.
	 */
	@Override
	public List<ColumnDetailsDTO> fetchColumnDetails(String schemaString) {
		LOGGER.info("fetch table, column, and its datatype service method");
		List<ColumnDetailsDTO> columnDetails;
		try {
			columnDetails = queryBuilderDao.fetchColumnDetails(schemaString);
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch ColumnDetailsDTO", exception);
			throw new DataAccessResourceFailureException("An error occurred while fetch ColumnDetailsDTO.");
		}
		LOGGER.debug("Result of table, column, and its datatype {}", columnDetails);
		return columnDetails;
	}

	/**
	 * @param queryString
	 * @return List<Map<String, Object>> By getting string as query in parameter
	 *         based on the query in will execute.
	 */
	@Override
	public List<Map<String, Object>> fetchResultData(String queryString) {
		LOGGER.info("fetch Result Data service");
		List<Map<String, Object>> responseList = new ArrayList<>();
		try {
			responseList = queryBuilderDao.fetchResultData(queryString);
		} catch (Exception exception) {
			LOGGER.error("An error occurred while fetch Data in service layer.", exception);
			throw new DataAccessResourceFailureException("An error occurred while fetch Data");
		}
		return responseList;
	}

	/**
	 * @param filterData
	 * @return String This method build the query based on the request. In this
	 *         method get request from pojo based on the request this method will
	 *         send data to dao layer. select query with and without where caluse
	 */
	@Override
	public String fetchQuery(FilterDataPOJO filterData, String schemaString) {
		LOGGER.info("fetch query service");
		StringBuilder querBuilder = new StringBuilder();
		try {
			querBuilder.append(QueryConstants.SELECT).append(String.join(",", filterData.getColumnNames()))
					.append(QueryConstants.FROM).append(schemaString).append(".").append(filterData.getTableName());
			if (!CollectionUtils.isEmpty(filterData.getJoin())) {
				querBuilder.append(getOnCondition(filterData.getJoin(), schemaString));
			}
			if (!CollectionUtils.isEmpty(filterData.getWhereData())) {
				querBuilder.append(QueryConstants.WHERE)
						.append(whereCondition(filterData.getWhereData(), getDataType(filterData, schemaString)));
			}

			if (filterData.getLimit() > 0) {
				querBuilder.append(getLimit(filterData));
			}
			if (filterData.getOrderBy() != null && !filterData.getOrderBy().toString().isEmpty()) {
				querBuilder.append(getColumnOrderBy(filterData.getOrderBy()));
			}

		} catch (Exception exception) {
			LOGGER.error("An error occurred while fetch Query.");
			throw new DataAccessResourceFailureException("An error occurred while fetch Query.", exception);
		}
		LOGGER.debug("Build Query for the request data service:{}", querBuilder);
		return querBuilder.toString();
	}

	/**
	 * @param schemaString
	 * @return This method will check the schema name and table exist in dao.
	 * 
	 */
	@Override
	public Boolean isSchemaExist(String schemaNameString) {
		LOGGER.info("Schema Exist service Method");
		boolean isSchemaExist = false;
		try {
			isSchemaExist = queryBuilderDao.isSchemaExist(schemaNameString);
		} catch (Exception exception) {
			LOGGER.error("An error occurred while checking is Schema Exist in service.");
			throw new DataAccessResourceFailureException("An error occurred while checking is Schema Exist.",
					exception);
		}
		return isSchemaExist;
	}

	/**
	 * @param schemaString
	 * @param tableName
	 * @return This method will check the schema and table and column in dao.
	 */
	@Override
	public Boolean isValidTable(String schemaString, String tableName, List<JoinDataPOJO> joinData) {
		LOGGER.info("isValid table service");
		Boolean isValidTable = false;
		try {
			Set<String> tablesList = new HashSet<>();
			if (!CollectionUtils.isEmpty(joinData)) {
				for (JoinDataPOJO joinTable : joinData) {
					tablesList.add(joinTable.getJoinTableName());
				}
			}
			tablesList.add(tableName);
			isValidTable = queryBuilderDao.isValidTable(schemaString, tablesList);
		} catch (Exception exception) {
			LOGGER.error("An error occurred while checking is valid Table.");
			throw new DataAccessResourceFailureException("An error occurred while checking is valid Table.", exception);

		}
		return isValidTable;

	}

	/**
	 * get the column valid using columnList with its table and schema
	 * 
	 * @param columnList
	 * @param tableName
	 * @param schemaString
	 */
	@Override
	public Boolean isValidColumns(List<String> columnList, List<WhereGroupListPOJO> whereConditionList,
			String tableName, String schemaString, List<JoinDataPOJO> joinData) {
		LOGGER.info("Is Valid TableDetailPOJO service method");
		boolean isValidColumn = false;
		try {
			Set<String> tablesList = new HashSet<>();
			Set<String> columnsList = new HashSet<>();

			if (!CollectionUtils.isEmpty(joinData)) {
				for (JoinDataPOJO joinTable : joinData) {
					tablesList.add(joinTable.getJoinTableName());
					for (JoinConditionPOJO joinConditionDto : joinTable.getJoinCondition()) {
						columnsList.add(joinConditionDto.getLsColumn());
						columnList.add(joinConditionDto.getRsColumn());
					}
				}
			}
			if (!CollectionUtils.isEmpty(whereConditionList)) {
				for (WhereGroupListPOJO whereGroupListDto : whereConditionList) {
					for (WhereListPOJO whereListDto : whereGroupListDto.getWhereList()) {
						columnsList.add(whereListDto.getColumn());
					}
				}
			}
			columnsList.addAll(columnList);
			tablesList.add(tableName);
			isValidColumn = queryBuilderDao.isValidColumns(columnsList, tablesList, schemaString);
		} catch (Exception exception) {
			LOGGER.error("An error occurred Checking is valid TableDetailPOJO.");
			throw new DataAccessResourceFailureException("An error occurred Checking is valid TableDetailPOJO.",
					exception);
		}
		return isValidColumn;
	}

	/**
	 * @param filterData
	 * @return get the datatype of column in the whereClause
	 */
	private Map<String, Object> getDataType(FilterDataPOJO filterData, String schemaString) {
		LOGGER.info("Get data type service");
		Map<String, Object> schemaMap = new LinkedHashMap<>();
		try {
			Set<String> tablesList = new HashSet<>();
			Set<String> columnsList = new HashSet<>();
			List<WhereGroupListPOJO> whereClauseList = filterData.getWhereData();
			for (WhereGroupListPOJO whereGroupListDto : whereClauseList) {
				List<WhereListPOJO> whereList = whereGroupListDto.getWhereList();
				for (WhereListPOJO whereListDto : whereList) {
					columnsList.add(whereListDto.getColumn());
				}
			}
			if (!CollectionUtils.isEmpty(filterData.getJoin())) {
				for (JoinDataPOJO joinTable : filterData.getJoin()) {
					tablesList.add(joinTable.getJoinTableName());
				}
			}
			tablesList.add(filterData.getTableName());
			List<ColumnDatatypeDTO> columnDetails = queryBuilderDao.getDataType(schemaString, tablesList, columnsList);
			for (ColumnDatatypeDTO columnDetail : columnDetails) {
				schemaMap.put(columnDetail.getTableColumn(), columnDetail.getDataType());
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred while Getting Data Type.");
			throw new DataAccessResourceFailureException("An error occurred while Getting Data Type.", exception);
		}
		LOGGER.debug("get datatype service:{}", schemaMap);
		return schemaMap;

	}

	/**
	 * This method build on condition in joins using string builder.
	 * 
	 * @param joinDataList
	 * @param schemaString
	 * @return
	 */
	private String getOnCondition(List<JoinDataPOJO> joinDataList, String schemaString) {
		LOGGER.info("build On condition using string builder method");
		StringBuilder conditionBuilder = new StringBuilder();
		try {
			for (JoinDataPOJO joinData : joinDataList) {

				conditionBuilder.append(" ").append(joinData.getJoinType().getOperator()).append(" ")
						.append(schemaString).append(".").append(joinData.getJoinTableName()).append(" ").append("ON");
				for (JoinConditionPOJO joinConditionDto : joinData.getJoinCondition()) {
					conditionBuilder.append(" ").append("(").append(joinConditionDto.getLsColumn()).append(" ")
							.append(joinConditionDto.getCondition().getOperator()).append(" ")
							.append(joinConditionDto.getRsColumn()).append(")");
					if (joinConditionDto.getLogicalCondition() != null) {
						conditionBuilder.append(" ").append(joinConditionDto.getLogicalCondition());
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred while Getting Join condition query.");
			throw new DataAccessResourceFailureException("An error occurred while getting Join condition query.",
					exception);
		}
		LOGGER.debug("On conditon : {}", conditionBuilder);
		return conditionBuilder.toString();
	}

	/**
	 * The method build the where condition by iterating the whereGroup data and
	 * where list data
	 * 
	 * @param filterData
	 * @param datatypeMap
	 * @return
	 */
	private String whereCondition(List<WhereGroupListPOJO> whereClauseList, Map<String, Object> columnDataTypeMap) {
		LOGGER.info("building where condition method");
		StringBuilder whereBuilder = new StringBuilder();
		try {
			Set<String> operatorString = new HashSet<>(
					Arrays.asList("varchar", "char", "enum", "text", "date", "time", "timestamp", "year"));
			for (WhereGroupListPOJO whereGroupListDto : whereClauseList) {
				whereBuilder.append("(");
				for (WhereListPOJO whereListDto : whereGroupListDto.getWhereList()) {
					whereBuilder.append(whereListDto.getColumn()).append(" ")
							.append(whereListDto.getCondition().getOperator());
					if (operatorString.contains(columnDataTypeMap.get(whereListDto.getColumn()))
							&& Condition.BETWEEN.equals(whereListDto.getCondition())) {
						ObjectMapper mapper = new ObjectMapper();
						ValuesPOJO value = mapper.readValue(mapper.writeValueAsString(whereListDto.getValue()),
								ValuesPOJO.class);
						whereBuilder.append(" '").append(value.getFrom()).append("' ").append("AND ").append("'")
								.append(value.getTo()).append("'");
					} else if (operatorString.contains(columnDataTypeMap.get(whereListDto.getColumn()))
							&& Condition.IN.equals(whereListDto.getCondition())) {
						List<String> list = (List<String>) whereListDto.getValue();
						String value = list.stream().collect(Collectors.joining("','", "'", "'"));
						whereBuilder.append(" (").append(value).append(")");
					}
					// check whether the column data type is a part of operater list to add single
					// quotes in prefix and suffix
					else if (operatorString.contains(columnDataTypeMap.get(whereListDto.getColumn()))) {
						whereBuilder.append("'").append(whereListDto.getValue()).append("'");
					} else {
						whereBuilder.append(whereListDto.getValue());
					}
					// Append condition to the where group list if the condition has value
					// Condition will be null if it is the last item of the list.
					if (whereListDto.getLogicalCondition() != null) {
						whereBuilder.append(" ").append(whereListDto.getLogicalCondition()).append(" ");
					}
				}
				whereBuilder.append(")");
				// Append condition to the where list if the condition has value
				// Condition will be null if it is the last item of the list
				if (whereGroupListDto.getLogicalCondition() != null) {
					whereBuilder.append(" ").append(whereGroupListDto.getLogicalCondition().name()).append(" ");
				}
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred while building where condition.");
			throw new DataAccessResourceFailureException("An error occurred while building where condition.",
					exception);
		}
		LOGGER.debug("where Condition:{}", whereBuilder);
		return whereBuilder.toString();
	}

	private String getLimit(FilterDataPOJO filterData) {
		StringBuilder querBuilder = new StringBuilder();
		if (filterData.getPageNo() > 0) {
			querBuilder.append(QueryConstants.LIMIT).append(filterData.getLimit()).append(QueryConstants.OFFSET)
					.append(filterData.getLimit() * (filterData.getPageNo() - 1));
		} else {
			querBuilder.append(QueryConstants.LIMIT).append(filterData.getLimit());
		}
		return querBuilder.toString();
  }
	private String getColumnOrderBy(List<OrderByPOJO> orderByPOJO) {
		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder orderBy = new StringBuilder();
		orderBy.append(QueryConstants.ORDERBY);
		for (OrderByPOJO columnList : orderByPOJO) {
			if (!columnBuilder.isEmpty()) {
				columnBuilder.append(",");
			}
			columnBuilder.append(columnList.getOrderColumnName()).append(" ").append(columnList.getOrderType());
		}
		orderBy.append(columnBuilder.toString());
		return orderBy.toString();
	}

}
