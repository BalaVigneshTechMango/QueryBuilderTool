package com.tm.querybuilder.service.impl;

import java.util.ArrayList;
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
import com.tm.querybuilder.keyword.KeyTypes;
import com.tm.querybuilder.pojo.FilterDataPOJO;
import com.tm.querybuilder.pojo.JoinConditionPOJO;
import com.tm.querybuilder.pojo.JoinDataPOJO;
import com.tm.querybuilder.pojo.ConditionGroupPOJO;
import com.tm.querybuilder.pojo.ConditionPOJO;
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
	public Boolean isValidTable(String schemaString, String tableString, List<JoinDataPOJO> joinDataList) {
		LOGGER.info("isValid table service");
		Boolean isValidTable = false;
		try {
			Set<String> tablesList = new HashSet<>();
			if (!CollectionUtils.isEmpty(joinDataList)) {
				for (JoinDataPOJO joinTable : joinDataList) {
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
	 * get the column valid using columnList with its table and schema
	 * 
	 * @param columnList
	 * @param tableName
	 * @param schemaString
	 */
	@Override
	public Boolean isValidColumns(List<String> columnList, List<ConditionGroupPOJO> conditionGroupList,
			String tableName, String schemaString, List<JoinDataPOJO> joinDataList) {
		LOGGER.info("Is Valid TableDetailPOJO service method");
		boolean isValidColumn = false;
		try {
			Set<String> tablesList = new HashSet<>();
			Set<String> columnsList = new HashSet<>();
			if (!CollectionUtils.isEmpty(joinDataList)) {
				for (JoinDataPOJO joinTable : joinDataList) {
					tablesList.add(joinTable.getJoinTableName());
					for (JoinConditionPOJO joinConditionDto : joinTable.getJoinCondition()) {
						columnsList.add(joinConditionDto.getLsColumn());
						columnList.add(joinConditionDto.getRsColumn());
					}
				}
			}
			if (!CollectionUtils.isEmpty(conditionGroupList)) {
				for (ConditionGroupPOJO whereGroupListDto : conditionGroupList) {
					for (ConditionPOJO whereListDto : whereGroupListDto.getConditionList()) {
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
		KeyTypes keyTypes = new KeyTypes();
		Clauses clauses = new Clauses();
		LOGGER.info("fetch query service");
		StringBuilder querBuilder = new StringBuilder();
		try {
			querBuilder.append(QueryConstants.SELECT).append(String.join(",", filterData.getColumnNames()));
			querBuilder.append(QueryConstants.FROM).append(schemaString).append(".").append(filterData.getTableName());
			if (!CollectionUtils.isEmpty(filterData.getJoin())) {
				querBuilder.append(clauses.getOnCondition(filterData.getJoin(), schemaString));
			}
			if (!CollectionUtils.isEmpty(filterData.getConditionData())) {
				querBuilder.append(clauses.whereCondition(filterData, getDataType(filterData, schemaString)));
			}
			if (EmptyNotNull.isValidInput(filterData.getGroupBy())) {
				querBuilder.append(clauses.groupBy(filterData.getGroupBy(), filterData.getColumnNames()));
				if (EmptyNotNull.isValidInput(filterData.getGroupBy().getConditionData())) {
					querBuilder.append(clauses.having(filterData.getGroupBy().getConditionData(),
							getDataType(filterData, schemaString)));
				}
			}
			if (EmptyNotNull.isValidInput(filterData.getOrderBy())) {
				querBuilder.append(keyTypes.getColumnOrderBy(filterData.getOrderBy()));
			}
			querBuilder.append(keyTypes.getLimit(filterData));
		} catch (Exception exception) {
			LOGGER.error("An error occurred while fetch Query.");
			throw new DataAccessResourceFailureException("An error occurred while fetch Query.", exception);
		}
		LOGGER.debug("Build Query for the request data service:{}", querBuilder);
		return querBuilder.toString();
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
			for (ConditionGroupPOJO conditionGroupList : filterData.getConditionData()) {
				for (ConditionPOJO conditions : conditionGroupList.getConditionList()) {
					columnsList.add(conditions.getColumn());
				}
			}
			if (!CollectionUtils.isEmpty(filterData.getJoin())) {
				for (JoinDataPOJO joinTable : filterData.getJoin()) {
					tablesList.add(joinTable.getJoinTableName());
				}
			}
			if (!CollectionUtils.isEmpty(filterData.getOrderBy())) {
				for (ConditionGroupPOJO conditionGroupList : filterData.getGroupBy().getConditionData()) {
					for (ConditionPOJO conditions : conditionGroupList.getConditionList()) {
						columnsList.add(conditions.getColumn());
					}
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

}
