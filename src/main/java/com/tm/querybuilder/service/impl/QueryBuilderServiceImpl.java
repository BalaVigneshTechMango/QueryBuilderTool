package com.tm.querybuilder.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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

	// The method get request from the BuilderRequestPojo to get the details of
	// table and columm by using schema name get the details with dao layer.
	@Override
	public Map<String, Map<String, String>> fetchColumnDetails(String schemaName) {
		try {
			List<String> tableList = new ArrayList<>();
			Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
			SqlRowSet rowSet = queryBuilderDao.fetchTableDetails(schemaName);
			// Iterate over the result set to get table names
			while (rowSet.next()) {
				tableList.add(rowSet.getString("table_name"));
			}
			// Iterate over the table names and execute the query to get column names and
			// data types
			for (String tableString : tableList) {
				Map<String, String> columnMap = new LinkedHashMap<>();
				SqlRowSet sqlRowSet = queryBuilderDao.fetchColumnDetails(schemaName, tableString);
				// Iterate over the result set to get column names and data types
				while (sqlRowSet.next()) {
					columnMap.put(sqlRowSet.getString(Constants.COLUMN_NAME), sqlRowSet.getString(Constants.DATA_TYPE));
				}
				// Add the table name and column names and data types to the schema map
				schemaMap.put(tableString, columnMap);
				// Add the table names to the schema map as a separate entry for easy access
			}
			Map<String, String> tableNameMap = new LinkedHashMap<>();
			tableNameMap.put("tableNames", String.join(",", tableList));
			schemaMap.put(Constants.TABLE_NAME, tableNameMap);
			return schemaMap;
		} catch (Exception exception) {
			exception.printStackTrace();
			return new HashMap<>();
		}

	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public List<Map<String, Object>> fetchResultData(String queryString) {
		try {
			return queryBuilderDao.fetchResultData(queryString);
		} catch (Exception exception) {
			exception.printStackTrace();
			return new ArrayList<>();
		}

	}

	// In this method get request from pojo based on the request this method will
	// send data to dao layer. select query with and without where caluse
	@Override
	public String fetchQuery(FilterData filterData) {
		try {
			WhereClause whereClause = new WhereClause();
			String schemaString = filterData.getSchemaName();
			String columnString = String.join(",", filterData.getColumnNames());
			String tableString = filterData.getTableName();
			// select query with where clause of single table
			if (filterData.getWhereData() != null) {
				return "Select " + columnString + " From " + schemaString + "." + tableString + " Where "
						+ whereClause.whereCondition(filterData, getDataType(filterData));
			} else {
				// select query without where clause of single table
				return "SELECT " + columnString + " FROM " + schemaString + "." + tableString;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "";
		}

	}

	// This method will check the data base name and table exist in dao.
	@Override
	public Integer schemaExistDetails(String schemaNameString) {
		try {
			return queryBuilderDao.schemaExistDetails(schemaNameString);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

	}

	// This method will check the schema and table and column in dao.
	@Override
	public boolean validateTable(String schemaString, String tableName) {
		try {
			return queryBuilderDao.validateTable(schemaString, tableName);
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean validateColumns(List<String> columnList, String tableName, String schemaString) {
		try {
			return queryBuilderDao.validateColumns(columnList, tableName, schemaString);
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}

	}

	@Override
	public Map<String, Map<String, String>> getDataType(FilterData filterData) {
		try {
			String tableString = filterData.getTableName();
			Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
			List<String> columnsList = new ArrayList<>();
			List<WhereGroupListDto> whereClauseList = filterData.getWhereData();
			for (int whereGroup = 0; whereGroup < whereClauseList.size(); whereGroup++) {
				for (int whereList = 0; whereList < filterData.getWhereData().get(whereGroup).getWhereList()
						.size(); whereList++) {
					columnsList.add(whereClauseList.get(whereGroup).getWhereList().get(whereList).getColumn());
				}
			}
			Map<String, String> columnMap = new LinkedHashMap<>();
			// while will get the column and datatype of the from rowset and put in
			// columnMap
			SqlRowSet rowSet = queryBuilderDao.getDataType(filterData.getSchemaName(), tableString, columnsList);
			while (rowSet.next()) {
				columnMap.put(rowSet.getString(Constants.COLUMN_NAME), rowSet.getString(Constants.DATA_TYPE));
			}
			schemaMap.put(tableString, columnMap);
			return schemaMap;

		} catch (Exception exception) {
			exception.printStackTrace();
			return new HashMap<>();
		}

	}

}
