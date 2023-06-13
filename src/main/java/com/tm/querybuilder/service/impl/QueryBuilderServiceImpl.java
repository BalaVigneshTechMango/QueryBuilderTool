package com.tm.querybuilder.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.common.WhereClause;
import com.tm.querybuilder.constant.Constants;
import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.dto.WhereGroupListDto;
import com.tm.querybuilder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	/**
	 * @param schemaName
	 * @return get table and columm by using schema name get the details with dao
	 *         layer.
	 */
	@Override
	public Map<String, Map<String, Object>> fetchColumnDetails(String schemaString) {
		Map<String, Map<String, Object>> schemaMap = new LinkedHashMap<>();
		try {
			List<String> tableList = queryBuilderDao.fetchTableDetails(schemaString);
			for (String tableString : tableList) {
				Map<String, Object> columnMap = queryBuilderDao.fetchColumnDetails(schemaString, tableString);
				schemaMap.put(tableString, columnMap);
			}
			Map<String, Object> tableNameMap = new LinkedHashMap<>();
			tableNameMap.put("tableNames", String.join(",", tableList));
			schemaMap.put(Constants.TABLE_NAME, tableNameMap);
		} catch (DataAccessException exception) {
			throw new DataAccessResourceFailureException("An error occurred while fetch ColumnDetails.", exception);

		}
		return schemaMap;

	}

	/**
	 * @param queryString
	 * @return List<Map<String, Object>> By getting string as query in parameter
	 *         based on the query in will execute.
	 */
	@Override
	public List<Map<String, Object>> fetchResultData(String queryString) {
		List<Map<String, Object>> responseList = new ArrayList<>();
		try {
			responseList = queryBuilderDao.fetchResultData(queryString);
		} catch (Exception exception) {
			throw new DataAccessResourceFailureException("An error occurred while fetch Data.", exception);

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
	public String fetchQuery(FilterData filterData) {
		String query = "";
		try {
			WhereClause whereClause = new WhereClause();
			String schemaString = filterData.getSchemaName();
			String columnString = String.join(",", filterData.getColumnNames());
			String tableString = filterData.getTableName();
			// select query with where clause of single table
			if (filterData.getWhereData() != null) {
				query = "Select " + columnString + " From " + schemaString + "." + tableString + " Where "
						+ whereClause.whereCondition(filterData, getDataType(filterData));
			} else {
				// select query without where clause of single table
				query = "SELECT " + columnString + " FROM " + schemaString + "." + tableString;
			}
		} catch (Exception exception) {
			throw new DataAccessResourceFailureException("An error occurred while fetch Query.", exception);

		}
		return query;
	}

	/**
	 * @param schemaString
	 * @return This method will check the schema name and table exist in dao.
	 * 
	 */
	@Override
	public Boolean isSchemaExist(String schemaNameString) {
		boolean isSchemaExist = false;
		try {
			isSchemaExist = queryBuilderDao.isSchemaExist(schemaNameString);
		} catch (Exception exception) {
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
		Boolean isValidTable = false;
		try {
			isValidTable = queryBuilderDao.isValidTable(schemaString, tableName);
		} catch (Exception exception) {
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
		boolean isValidColumn = false;
		try {
			isValidColumn = queryBuilderDao.isValidColumns(columnList, tableName, schemaString);
		} catch (Exception exception) {
			throw new DataAccessResourceFailureException("An error occurred Checking is valid Column.", exception);
		}
		return isValidColumn;
	}

	/**
	 * @param filterData
	 * @return get the datatype of column in the whereClause
	 */
	@Override
	public Map<String, Map<String, Object>> getDataType(FilterData filterData) {
		Map<String, Map<String, Object>> schemaMap = new LinkedHashMap<>();
		try {
			String tableString = filterData.getTableName();
			List<String> columnsList = new ArrayList<>();
			List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
			for (int whereGroup = 0; whereGroup < whereClauseList.size(); whereGroup++) {
				for (int whereList = 0; whereList < filterData.getWhereData().get(whereGroup).getWhereList()
						.size(); whereList++) {
					columnsList.add(whereClauseList.get(whereGroup).getWhereList().get(whereList).getColumn());
				}
			}
			Map<String, Object> columnMap = queryBuilderDao.getDataType(filterData.getSchemaName(), tableString,
					columnsList);
			schemaMap.put(tableString, columnMap);

		} catch (Exception exception) {
			throw new DataAccessResourceFailureException("An error occurred while Getting Data Type.", exception);
		}
		return schemaMap;

	}

}
