package com.tm.querybuilder.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.util.CollectionUtils;

import com.tm.querybuilder.clauses.Clauses;
import com.tm.querybuilder.constant.QueryConstants;
import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.ColumnDatatypeDTO;
import com.tm.querybuilder.dto.ColumnDetailsDTO;
import com.tm.querybuilder.dto.CountRowDTO;
import com.tm.querybuilder.dto.FetchTableDetailsDTO;
import com.tm.querybuilder.enums.KeyColumn;
import com.tm.querybuilder.keyword.KeyTypes;
import com.tm.querybuilder.pojo.FilterDataPOJO;
import com.tm.querybuilder.pojo.ForeignKeysPOJO;
import com.tm.querybuilder.pojo.JoinConditionPOJO;
import com.tm.querybuilder.pojo.JoinDataPOJO;
import com.tm.querybuilder.pojo.JoinsPOJO;
import com.tm.querybuilder.pojo.AggregateFunctionPOJO;
import com.tm.querybuilder.pojo.ConditionGroupPOJO;
import com.tm.querybuilder.pojo.ConditionPOJO;
import com.tm.querybuilder.pojo.FetchTableDetailsPOJO;
import com.tm.querybuilder.service.QueryBuilderService;
import com.tm.querybuilder.validation.EmptyNotNull;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryBuilderServiceImpl.class);

	/**
	 * @param schemaString
	 * @return This method will check the schema name and table exist in dao.
	 * 
	 */
	@Override
	public Boolean isSchemaExist(String schemaString) {
		LOGGER.info("Schema Exist service Method");
		boolean isSchemaExist = false;
		try {
			isSchemaExist = queryBuilderDao.isSchemaExist(schemaString);
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
	public Boolean isValidTable(String schemaString, String tableString, JoinsPOJO joinsPOJO) {
		LOGGER.info("isValid table service");
		Boolean isValidTable = false;
		try {

			Set<String> tablesList = new HashSet<>();
			if (EmptyNotNull.isValidInput(joinsPOJO)) {
				for (JoinDataPOJO joinTable : joinsPOJO.getJoin()) {
					tablesList.add(joinTable.getJoinTableName());
				}
			}
			tablesList.add(tableString);
			isValidTable = queryBuilderDao.isValidTable(schemaString, tablesList);
		} catch (Exception exception) {
			LOGGER.error("An error occurred while checking is valid Table.");
			throw new DataAccessResourceFailureException("An error occurred while checking is valid Table.", exception);
		}
		return isValidTable;
	}

	/**
	 * @param schemaName
	 * @return get table and columm by using schema name get the details with dao
	 *         layer.
	 */
	@Override
	public List<ColumnDetailsDTO> fetchColumnDetails(String schemaString) {
		LOGGER.info("fetch table, column, and its datatype service method");
		List<ColumnDetailsDTO> columnDetailsList;
		try {
			columnDetailsList = queryBuilderDao.fetchColumnDetails(schemaString);
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch ColumnDetailsDTO", exception);
			throw new DataAccessResourceFailureException("An error occurred while fetch ColumnDetailsDTO.");
		}
		LOGGER.debug("Result of table, column, and its datatype {}", columnDetailsList);
		return columnDetailsList;
	}

	/**
	 * @param queryString
	 * @return List<Map<String, Object>> By getting string as query in parameter
	 *         based on the query in will execute.
	 */
	private List<CountRowDTO> fetchCountQuery(String countqueryString) {
		LOGGER.info("fetch Result Data service");
		List<CountRowDTO> countQuery;
		try {
			countQuery = queryBuilderDao.countQuery(countqueryString);
		} catch (Exception exception) {
			LOGGER.error("An error occurred while fetch Data in service layer.", exception);
			throw new DataAccessResourceFailureException("An error occurred while fetch Data");
		}
		return countQuery;
	}

	/**
	 * @param queryString
	 * @return List<Map<String, Object>> By getting string as query in parameter
	 *         based on the query in will execute.
	 */
	@Override
	public List<Map<String, Object>> fetchResultData(String selectQueryString, String countQueryString) {
		LOGGER.info("fetch Result Data service");
		List<Map<String, Object>> responseList = new ArrayList<>();
		Map<String, Object> countMap = new HashMap<>();
		try {
			responseList = queryBuilderDao.fetchResultData(selectQueryString);
			if (!CollectionUtils.isEmpty(responseList)) {
				countMap.put("rowCount", fetchCountQuery(countQueryString).get(0).getCount());
				responseList.add(countMap);
			}
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
	public Map<String, String> fetchQuery(FilterDataPOJO filterData, String schemaString) {
		Map<String, String> queryMap = new HashMap<>();
		KeyTypes keyTypes = new KeyTypes();
		LOGGER.info("fetch query service");
		StringBuilder querBuilder = new StringBuilder();
		StringBuilder selectBuilder = new StringBuilder();
		try {
			selectBuilder.append(QueryConstants.SELECT);
			if (EmptyNotNull.isValidInput(filterData.getGroupBy())
					&& !CollectionUtils.isEmpty(filterData.getColumnNames())
					&& !CollectionUtils.isEmpty(filterData.getAggregateFunction())) {
				selectBuilder.append(String.join(",", filterData.getColumnNames()));
				for (AggregateFunctionPOJO aggregateFunctionPOJO : filterData.getAggregateFunction()) {
					selectBuilder.append(aggregateFunctionPOJO.getAggregateTypes()).append("(")
							.append(aggregateFunctionPOJO.getColumnName()).append(")");
				}
			} else if (!CollectionUtils.isEmpty(filterData.getColumnNames())) {
				selectBuilder.append(String.join(",", filterData.getColumnNames()));
			} else if (!CollectionUtils.isEmpty(filterData.getAggregateFunction())) {
				for (AggregateFunctionPOJO aggregateFunctionPOJO : filterData.getAggregateFunction()) {
					selectBuilder.append(aggregateFunctionPOJO.getAggregateTypes()).append("(")
							.append(aggregateFunctionPOJO.getColumnName()).append(")");
				}
			} else {
				LOGGER.error("** Both the column list and aggregate function are empty **");
				throw new DataAccessResourceFailureException("Both the column List and aggregate function are empty");
			}
			querBuilder.append(QueryConstants.FROM).append(schemaString).append(".").append(filterData.getTableName());
			String ifQueryPresent = ifQueryBuilder(filterData, schemaString);
			if (EmptyNotNull.isValidInput(ifQueryPresent)) {
				querBuilder.append(ifQueryPresent);
			}
			if (EmptyNotNull.isValidInput(filterData.getOrderBy())) {
				querBuilder.append(keyTypes.getColumnOrderBy(filterData.getOrderBy()));
			}
            //querBuilder.append(keyTypes.getLimit(filterData));
		} catch (Exception exception) {
			LOGGER.error("An error occurred while fetch Query.");
			throw new DataAccessResourceFailureException("An error occurred while fetch Query.", exception);
		}
		queryMap.put("selectQuery", selectBuilder.append(querBuilder).toString());
		StringBuilder countBuilder = new StringBuilder();
		countBuilder.append(QueryConstants.SELECT_COUNT);
		queryMap.put("countQuery", countBuilder.append(querBuilder).toString());

		LOGGER.debug("Build Query for the request data service:{}", querBuilder);

		return queryMap;
	}

	private String ifQueryBuilder(FilterDataPOJO filterData, String schemaString) {
		StringBuilder querBuilder = new StringBuilder();
		Clauses clauses = new Clauses();
		try {
			if (EmptyNotNull.isValidInput(filterData.getJoinData())) {
				querBuilder.append(clauses.getOnCondition(filterData.getJoinData().getJoin(), schemaString));
			}
			if (EmptyNotNull.isValidInput(filterData.getWhereData())) {
				querBuilder.append(clauses.whereCondition(filterData, getDataType(filterData, schemaString)));
			}
			if (EmptyNotNull.isValidInput(filterData.getGroupBy())) {
				if (!CollectionUtils.isEmpty(filterData.getGroupBy().getColumnList())) {
					querBuilder.append(clauses.groupBy(filterData.getGroupBy(), filterData.getColumnNames()));
				}
				if (!CollectionUtils.isEmpty(filterData.getGroupBy().getConditionData())) {
					querBuilder.append(clauses.having(filterData.getGroupBy().getConditionData(),
							getDataType(filterData, schemaString)));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred while ifQuery Builder.");
			throw new DataAccessResourceFailureException("An error occurred while ifQuery Builder.", exception);
		}
		LOGGER.debug("Build Query for the request data service:{}", querBuilder);
		return querBuilder.toString();

	}

	/**
	 * @param filterData
	 * @return Get the datatype of column in the whereClause
	 */
	private Map<String, Object> getDataType(FilterDataPOJO filterData, String schemaString) {
		LOGGER.info("Get data type service");
		Map<String, Object> schemaMap = new LinkedHashMap<>();
		try {
			Set<String> tablesList = new HashSet<>();
			Set<String> columnsList = new HashSet<>();
			if (!CollectionUtils.isEmpty(filterData.getWhereData().getConditionData())) {
				for (ConditionGroupPOJO conditionGroupList : filterData.getWhereData().getConditionData()) {
					for (ConditionPOJO conditions : conditionGroupList.getConditionList()) {
						columnsList.add(conditions.getColumn());
					}
				}
			}
			if (EmptyNotNull.isValidInput(filterData.getGroupBy())) {
				columnsList.addAll(filterData.getColumnNames());
				if (!CollectionUtils.isEmpty(filterData.getGroupBy().getConditionData())) {
					for (ConditionGroupPOJO condition : filterData.getGroupBy().getConditionData()) {
						for (ConditionPOJO conditions : condition.getConditionList()) {
							columnsList.add(conditions.getColumn());
						}
					}
				}
			}
			if (EmptyNotNull.isValidInput(filterData.getJoinData())) {
				for (JoinDataPOJO joinTable : filterData.getJoinData().getJoin()) {
					tablesList.add(joinTable.getJoinTableName());
				}
			}
			tablesList.add(filterData.getTableName());
			List<ColumnDatatypeDTO> columnDetails = queryBuilderDao.getDataType(schemaString, tablesList, columnsList);
			for (ColumnDatatypeDTO columnDetail : columnDetails) {
				schemaMap.put(columnDetail.getTableColumn(), columnDetail.getDataType());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			LOGGER.error("An error occurred while Getting Data Type.");
			throw new DataAccessResourceFailureException("An error occurred while Getting Data Type.", exception);
		}
		LOGGER.debug("get datatype service:{}", schemaMap);
		return schemaMap;
	}

	/**
	 * get the column valid using columnList with its table and schema
	 * 
	 * @param columnList
	 * @param tableName
	 * @param schemaString
	 */
	@Override
	public Boolean isValidColumns(FilterDataPOJO filterData, String schemaString) {
		LOGGER.info("Is Valid TableDetailPOJO service method");
		boolean isValidColumn = false;
		try {
			Set<String> tablesList = new HashSet<>();
			Set<String> columnsList = new HashSet<>();
			if (EmptyNotNull.isValidInput(filterData.getJoinData())) {
				for (JoinDataPOJO joinTable : filterData.getJoinData().getJoin()) {
					tablesList.add(joinTable.getJoinTableName());
					for (JoinConditionPOJO joinConditionDto : joinTable.getJoinCondition()) {
						columnsList.add(joinConditionDto.getLsColumn());
						columnsList.add(joinConditionDto.getRsColumn());
					}
				}
			}
			if (EmptyNotNull.isValidInput(filterData.getWhereData())) {
				for (ConditionGroupPOJO conditionGroupList : filterData.getWhereData().getConditionData()) {
					for (ConditionPOJO conditionList : conditionGroupList.getConditionList()) {
						columnsList.add(conditionList.getColumn());
					}
				}
			}
			if (EmptyNotNull.isValidInput(filterData.getGroupBy())) {
				if (!CollectionUtils.isEmpty(filterData.getGroupBy().getColumnList())) {
					columnsList.addAll(filterData.getGroupBy().getColumnList());
				}
				if (!CollectionUtils.isEmpty(filterData.getGroupBy().getConditionData())) {
					for (ConditionGroupPOJO conditionGroupList : filterData.getGroupBy().getConditionData()) {
						for (ConditionPOJO conditionList : conditionGroupList.getConditionList()) {
							columnsList.add(conditionList.getColumn());
						}
					}
				}
			}
			if (!CollectionUtils.isEmpty(filterData.getColumnNames())) {
				columnsList.addAll(filterData.getColumnNames());
			} else {
				for (AggregateFunctionPOJO aggregateFunction : filterData.getAggregateFunction()) {
					columnsList.add(aggregateFunction.getColumnName());
				}
			}
			tablesList.add(filterData.getTableName());
			isValidColumn = queryBuilderDao.isValidColumns(columnsList, tablesList, schemaString);
		} catch (Exception exception) {
			LOGGER.error("An error occurred Checking is valid Column details.");
			throw new DataAccessResourceFailureException("An error occurred Checking is valid Column Details",
					exception);
		}
		return isValidColumn;
	}

	private Map<String, FetchTableDetailsPOJO> fetchTableDetails(FilterDataPOJO filterDataPOJO, String schemaString) {
		Map<String, FetchTableDetailsPOJO> tablesMap = new HashMap<>();
		try {
			Set<String> tablesList = new HashSet<>();
			if (!CollectionUtils.isEmpty(filterDataPOJO.getJoinData().getJoin())) {
				for (JoinDataPOJO joinTable : filterDataPOJO.getJoinData().getJoin()) {
					tablesList.add(joinTable.getJoinTableName());
				}
			}
			tablesList.add(filterDataPOJO.getTableName());
			List<FetchTableDetailsDTO> fetchTableDetails = queryBuilderDao.fetchTableDetails(tablesList, schemaString);
			for (FetchTableDetailsDTO fetchTableDetailsDTO : fetchTableDetails) {
				FetchTableDetailsPOJO fetchTableDetailsPOJO = new FetchTableDetailsPOJO();
				if (!tablesMap.containsKey(fetchTableDetailsDTO.getTableName())) {
					tablesMap.put(fetchTableDetailsDTO.getTableName(), fetchTableDetailsPOJO);
				}
				if (KeyColumn.PRI.equals(fetchTableDetailsDTO.getColumnKey())) {
					fetchTableDetailsPOJO = tablesMap.get(fetchTableDetailsDTO.getTableName());
					fetchTableDetailsPOJO.setPrimarykey(
							fetchTableDetailsDTO.getTableName() + "." + fetchTableDetailsDTO.getColumnName());
				} else if (KeyColumn.MUL.equals(fetchTableDetailsDTO.getColumnKey())) {
					List<ForeignKeysPOJO> foreignKeyList = new ArrayList<>();
					ForeignKeysPOJO foreignKeys = new ForeignKeysPOJO();
					foreignKeys.setColumnName(fetchTableDetailsDTO.getColumnName());
					foreignKeys.setReferencecolumn(fetchTableDetailsDTO.getReferenceColumn());
					foreignKeys.setReferenceTable(fetchTableDetailsDTO.getReferenceTable());
					fetchTableDetailsPOJO = tablesMap.get(fetchTableDetailsDTO.getTableName());
					foreignKeyList.add(foreignKeys);
					fetchTableDetailsPOJO.setForeignKeys(foreignKeyList);
				}
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred Checking is valid Column details.");
			throw new DataAccessResourceFailureException("An error occurred Checking is valid Column Details",
					exception);
		}
		return tablesMap;
	}

	@Override
	public Boolean joinConditionValidator(FilterDataPOJO filterData, String schemaString) {
		boolean isValidCondition = false;
		try {
			Map<String, FetchTableDetailsPOJO> tableDetails = fetchTableDetails(filterData, schemaString);
			isValidCondition = conditionValidator(filterData, tableDetails);
		} catch (Exception exception) {
			LOGGER.error("** An error occurred while validating the join condition in service **");
			throw new DataAccessResourceFailureException("An error occurred while validating the join condition.",
					exception);
		}
		LOGGER.debug("On conditon : {}", isValidCondition);
		return isValidCondition;
	}

	private Boolean conditionValidator(FilterDataPOJO filterData, Map<String, FetchTableDetailsPOJO> tableDetails) {
		LOGGER.info("build On condition using string builder method");
		boolean isValid = false;
		try {
			for (JoinDataPOJO joinData : filterData.getJoinData().getJoin()) {
				for (JoinConditionPOJO joinConditionDto : joinData.getJoinCondition()) {
					FetchTableDetailsPOJO leftTableString = tableDetails.get(filterData.getTableName());
					FetchTableDetailsPOJO rightTableString = tableDetails.get(joinData.getJoinTableName());
					if (leftTableString.getPrimarykey().equals(joinConditionDto.getLsColumn())
							&& rightTableString.getPrimarykey().equals(joinConditionDto.getRsColumn())) {
						isValid = false;
					} else {
						boolean isPrimary = false;
						boolean isForeign = false;
						isPrimary = leftTableString.getPrimarykey().equals(joinConditionDto.getLsColumn())
								|| rightTableString.getPrimarykey().equals(joinConditionDto.getRsColumn());
						if (!CollectionUtils.isEmpty(leftTableString.getForeignKeys())) {
							for (ForeignKeysPOJO foreignKey : leftTableString.getForeignKeys()) {
								isForeign = foreignKey.getReferenceTable().equals(joinData.getJoinTableName())
										&& foreignKey.getReferencecolumn().equals(joinConditionDto.getLsColumn());
							}
						}
						if (!CollectionUtils.isEmpty(rightTableString.getForeignKeys())) {
							for (ForeignKeysPOJO foreignKeysPOJO : rightTableString.getForeignKeys()) {
								isForeign = foreignKeysPOJO.getReferenceTable().equals(filterData.getTableName())
										&& foreignKeysPOJO.getReferencecolumn().equals(joinConditionDto.getRsColumn());
							}
						}
						isValid = Boolean.TRUE.equals(isPrimary) && Boolean.TRUE.equals(isForeign);
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("An error occurred while Getting Join condition query.");
			throw new DataAccessResourceFailureException("An error occurred while getting Join condition query.",
					exception);
		}
		LOGGER.debug("On conditon : {}", isValid);
		return isValid;
	}
}
