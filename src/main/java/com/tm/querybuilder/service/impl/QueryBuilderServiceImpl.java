package com.tm.querybuilder.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.constant.QueryConstants;
import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.ColumnDatatype;
import com.tm.querybuilder.dto.ColumnDetails;
import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.dto.JoinConditionDto;
import com.tm.querybuilder.dto.JoinData;
import com.tm.querybuilder.dto.WhereGroupListDto;
import com.tm.querybuilder.dto.WhereListDto;
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
	public Map<String, Map<String, Object>> fetchColumnDetails(String schemaString) {
		LOGGER.info("fetch table,column and its datatype service method");
		Map<String, Map<String, Object>> schemaMap = new LinkedHashMap<>();
		try {
			List<ColumnDetails> columnDetailsList = queryBuilderDao.fetchColumnDetails(schemaString);
			for (ColumnDetails columnDetails : columnDetailsList) {
				String tableName = columnDetails.getTableName();
				if (!schemaMap.containsKey(tableName)) {
					schemaMap.put(tableName, new LinkedHashMap<>());
				}
				schemaMap.get(tableName).put(columnDetails.getColumnName(), columnDetails.getDataType());
			}
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch ColumnDetails.", exception);
			throw new DataAccessResourceFailureException("An error occurred while fetch ColumnDetails.");
		}
		LOGGER.debug("Result of table,column and its datatype {}", schemaMap);
		return schemaMap;
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
			throw new DataAccessResourceFailureException("An error occurred while fetch Data .");
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
	public String fetchQuery(FilterData filterData, String schemaString) {
		LOGGER.info("fetch query service");
		String query = "";
		try {
			String columnString = String.join(",", filterData.getColumnNames());
			List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
			List<JoinData> joinsList = filterData.getJoin();
			if (whereClauseList != null && !whereClauseList.isEmpty() && joinsList != null && !joinsList.isEmpty()) {
				LOGGER.info("select query using Joins with whereClause");
				String joinConditionString = getOnCondition(joinsList, schemaString, filterData.getTableName());
				query = QueryConstants.SELECT + columnString + QueryConstants.FROM + joinConditionString + " WHERE "
						+ whereCondition(whereClauseList, getDataType(filterData, schemaString));
			} else if (joinsList != null && !joinsList.isEmpty()) {
				LOGGER.info("select query with join condition");
				String joinConditionString = getOnCondition(joinsList, schemaString, filterData.getTableName());
				query = QueryConstants.SELECT + columnString + QueryConstants.FROM + joinConditionString;
			}
			// select query with where clause of single table
			else if (whereClauseList != null && !whereClauseList.isEmpty()) {
				LOGGER.info("select query with whereClause");
				query = QueryConstants.SELECT + columnString + QueryConstants.FROM + schemaString + "."
						+ filterData.getTableName() + " WHERE "
						+ whereCondition(whereClauseList, getDataType(filterData, schemaString));
			} else {
				LOGGER.info("Select Query without whereClause");
				// select query without where clause of single table
				query = QueryConstants.SELECT + columnString + QueryConstants.FROM + schemaString + "."
						+ filterData.getTableName();
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred while fetch Query.");
			throw new DataAccessResourceFailureException("An error occurred while fetch Query.", exception);
		}
		LOGGER.debug("Build Query for the request data service:{}", query);
		return query;
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
	public Boolean isValidTable(String schemaString, String tableName, List<JoinData> joinData) {
		LOGGER.info("isValid table service");
		Boolean isValidTable = false;
		try {
			Set<String> tablesList = new HashSet<>();
			if (joinData != null && !joinData.isEmpty()) {
				for (JoinData joinTable : joinData) {
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
	 * @param columnList
	 * @param tableName
	 * @param schemaString get the column valid using columnList with its table and
	 *                     schema
	 */
	@Override
	public Boolean isValidColumns(List<String> columnList, List<WhereGroupListDto> whereCondition, String tableName,
			String schemaString, List<JoinData> joinData) {
		LOGGER.info("Is Valid Column service method");
		boolean isValidColumn = false;
		try {
			Set<String> tablesList = new HashSet<>();
			Set<String> columnsList = new HashSet<>();
			if (joinData != null && !joinData.isEmpty()) {
				for (JoinData joinTable : joinData) {
					tablesList.add(joinTable.getJoinTableName());
					for (JoinConditionDto joinConditionDto : joinTable.getJoinCondition()) {
						columnsList.add(tableName + '.' + joinConditionDto.getLsColumn());
						columnsList.add(joinTable.getJoinTableName() + '.' + joinConditionDto.getRsColumn());
					}
				}
			}
			if (whereCondition != null && !whereCondition.isEmpty()) {
				for (WhereGroupListDto whereGroupListDto : whereCondition) {
					for (WhereListDto whereListDto : whereGroupListDto.getWhereList()) {
						columnsList.add(whereListDto.getColumn());
					}
				}
			}
			columnsList.addAll(columnList);
			tablesList.add(tableName);
			isValidColumn = queryBuilderDao.isValidColumns(columnsList, tablesList, schemaString);
		} catch (Exception exception) {
			LOGGER.error("An error occurred Checking is valid Column.");
			throw new DataAccessResourceFailureException("An error occurred Checking is valid Column.", exception);
		}
		return isValidColumn;
	}

	/**
	 * @param filterData
	 * @return get the datatype of column in the whereClause
	 */
	private Map<String, Object> getDataType(FilterData filterData, String schemaString) {
		LOGGER.info("Get data type service");
		Map<String, Object> schemaMap = new LinkedHashMap<>();
		try {
			Set<String> tablesList = new HashSet<>();
			Set<String> columnsList = new HashSet<>();
			List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
			for (WhereGroupListDto whereGroupListDto : whereClauseList) {
				List<WhereListDto> whereList = whereGroupListDto.getWhereList();
				for (WhereListDto whereListDto : whereList) {
					columnsList.add(whereListDto.getColumn());
				}
			}
			if (filterData.getJoin() != null && !filterData.getJoin().isEmpty()) {
				for (JoinData joinTable : filterData.getJoin()) {
					tablesList.add(joinTable.getJoinTableName());
				}
			}
			tablesList.add(filterData.getTableName());
			List<ColumnDatatype> columnDetails = queryBuilderDao.getDataType(schemaString, tablesList, columnsList);
			for (ColumnDatatype columnDetail : columnDetails) {
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
	private String getOnCondition(List<JoinData> joinDataList, String schemaString, String tableName) {
		LOGGER.info("build On condition using string builder method");
		StringBuilder conditionBuilder = new StringBuilder();
		try {
			for (JoinData joinData : joinDataList) {
				conditionBuilder.append(schemaString).append(".").append(tableName).append(" ")
						.append(joinData.getJoinType().getOperator()).append(" ").append(schemaString).append(".")
						.append(joinData.getJoinTableName()).append(" ").append("ON");
				for (JoinConditionDto joinConditionDto : joinData.getJoinCondition()) {
					conditionBuilder.append(" ").append("(").append(" ").append(tableName).append(".")
							.append(joinConditionDto.getLsColumn()).append(" ")
							.append(joinConditionDto.getCondition().getOperator()).append(" ")
							.append(joinData.getJoinTableName()).append(".").append(joinConditionDto.getRsColumn())
							.append(") ");
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
	private String whereCondition(List<WhereGroupListDto> whereClauseList, Map<String, Object> columnDataTypeMap) {
		LOGGER.info("building where condition method");
		StringBuilder whereBuilder = new StringBuilder();
		try {
			Set<String> operatorString = new HashSet<>(Arrays.asList("varchar", "char", "enum", "text"));
			for (WhereGroupListDto whereGroupListDto : whereClauseList) {
				List<WhereListDto> whereList = whereGroupListDto.getWhereList();
				StringBuilder whereGroupBuilder = new StringBuilder();
				for (WhereListDto whereListDto : whereList) {
					whereGroupBuilder.append(whereListDto.getColumn())
							.append(whereListDto.getCondition().getOperator());
					// check whether the column data type is a part of operater list to add single
					// quotes in prefix and suffix
					if (operatorString.contains(columnDataTypeMap.get(whereListDto.getColumn()))) {
						whereGroupBuilder.append("'").append(whereListDto.getValue()).append("'");
					} else {
						whereGroupBuilder.append(whereListDto.getValue());
					}
					// Append condition to the where group list if the condition has value
					// Condition will be null if it is the last item of the list.
					if (whereListDto.getLogicalCondition() != null) {
						whereGroupBuilder.append(" ").append(whereListDto.getLogicalCondition()).append(" ");
					}
				}
				// Start and close with paranthesis if inner condition list has value
				// Append the where group string to the where list
				if (!whereGroupBuilder.toString().isEmpty()) {
					whereBuilder.append("(").append(whereGroupBuilder.toString()).append(")");
				}
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

}
