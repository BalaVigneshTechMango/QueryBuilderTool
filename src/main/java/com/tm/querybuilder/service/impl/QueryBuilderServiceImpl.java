package com.tm.querybuilder.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.common.WhereClause;
import com.tm.querybuilder.constant.MessageConstants;
import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.FilterData;
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
			List<String> tableList = queryBuilderDao.fetchTableDetails(schemaString);
			for (String tableString : tableList) {
				Map<String, Object> columnMap = queryBuilderDao.fetchColumnDetails(schemaString, tableString);
				schemaMap.put(tableString, columnMap);
			}
			Map<String, Object> tableNameMap = new LinkedHashMap<>();
			tableNameMap.put("tableNames", String.join(",", tableList));
			schemaMap.put(MessageConstants.TABLE_NAME, tableNameMap);
		} catch (DataAccessException exception) {
			LOGGER.error("An error occurred while fetch ColumnDetails.",exception);
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
			LOGGER.error("An error occurred while fetch Data in service layer.",exception);
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
			WhereClause whereClause = new WhereClause();
			// select query with where clause of single table
			if (filterData.getWhereData() != null) {
				LOGGER.info("select query with whereClause");
				query = "Select " + String.join(",", filterData.getColumnNames()) + " From " + schemaString + "."
						+ filterData.getTableName() + " Where "
						+ whereClause.whereCondition(filterData, getDataType(filterData, schemaString));
			} else {
				LOGGER.info("Select Query without whereClause");
				// select query without where clause of single table
				query = "SELECT " + String.join(",", filterData.getColumnNames()) + " FROM " + schemaString + "."
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
	public Boolean isValidTable(String schemaString, String tableName) {
		LOGGER.info("isValid table service");
		Boolean isValidTable = false;
		try {
			isValidTable = queryBuilderDao.isValidTable(schemaString, tableName);
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
	public Boolean isValidColumns(List<String> columnList, String tableName, String schemaString) {
		LOGGER.info("Is Valid Column service method");
		boolean isValidColumn = false;
		try {
			isValidColumn = queryBuilderDao.isValidColumns(columnList, tableName, schemaString);
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
	private Map<String, Map<String, Object>> getDataType(FilterData filterData, String schemaString) {
		LOGGER.info("Get data type service");
		Map<String, Map<String, Object>> schemaMap = new LinkedHashMap<>();
		try {
			String tableString = filterData.getTableName();
			List<String> columnsList = new ArrayList<>();
			List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
			for (WhereGroupListDto whereGroupListDto : whereClauseList) {
				List<WhereListDto> whereList = whereGroupListDto.getWhereList();
				for (WhereListDto whereListDto : whereList) {
					columnsList.add(whereListDto.getColumn());
				}
			}
			Map<String, Object> columnMap = queryBuilderDao.getDataType(schemaString, tableString, columnsList);
			schemaMap.put(tableString, columnMap);
		} catch (Exception exception) {
			LOGGER.error("An error occurred while Getting Data Type.");
			throw new DataAccessResourceFailureException("An error occurred while Getting Data Type.", exception);
		}
		LOGGER.debug("get datatype service:{}", schemaMap);
		return schemaMap;

	}

}
