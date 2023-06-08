package com.tm.querybuilder.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.request.BuilderRequestPojo;
import com.tm.querybuilder.response.QueryResponsePojo;
import com.tm.querybuilder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	private static final String QUERY = "query";

	// The method get request from the BuilderRequestPojo to get the details of
	// table and columm by using schema name get the details with dao layer.
	@Override
	public Map<String, Map<String, String>> getTableColumn(BuilderRequestPojo builderRequestPojo) {
		return queryBuilderDao.getTableColumn(builderRequestPojo.getSchemaName());
	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public Map<String, Object> getQueryExecution(Map<String, String> queryMap) {
		return queryBuilderDao.getQueryExecution(queryMap);

	}

	// In this method get request from pojo based on the request this method will
	// send data to dao layer. select query with and without where caluse
	@Override
	public Map<String, String> getQueryBuild(BuilderRequestPojo builderRequestPojo) {

		FilterData filterData = builderRequestPojo.getRequestData();
		String schemaString = filterData.getSchemaName();
		Map<String, String> previewQueryMap = new HashMap<>();
		String columnString = String.join(",", filterData.getColumnName());
		String tableString = filterData.getTableName();
		// select query with where clause of single table
		if (filterData.getWhereData() != null) {
			String sqlString = "Select " + columnString + " From " + schemaString + "." + tableString + " Where "
					+ queryBuilderDao.whereCondition(filterData);
			previewQueryMap.put(QUERY, sqlString);
		} else {
			// select query without where clause of single table
			String sqlString = "SELECT " + columnString + " FROM " + schemaString + "." + tableString;
			previewQueryMap.put(QUERY, sqlString);
		}
		return previewQueryMap;

	}

	// This method will check the data base name and table exist in dao.
	@Override
	public QueryResponsePojo schemaCheck(String schemaNameString, String databaseString) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		if (!schemaNameString.trim().isEmpty() && !databaseString.trim().isEmpty()) {
			Boolean schemaExistBoolean = queryBuilderDao.schemaExists(schemaNameString);
			if (Boolean.TRUE.equals(schemaExistBoolean)) {
				queryBuilderDao.checkTablesExistInSchema(schemaNameString);
				queryResponsePojo.setIsSuccess(true);
			} else {
				queryResponsePojo.setIsSuccess(false);
				queryResponsePojo.setMessage("Enter Valid Schema Name");
			}
		} else {
			queryResponsePojo.setMessage("Schema Name or Data Base Name is Empty");
			queryResponsePojo.setIsSuccess(false);
		}
		return queryResponsePojo;
	}

	// This method will check the schema and table and column in dao.
	@Override
	public QueryResponsePojo schemaTableColumn(FilterData filterData) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		String schemaString = filterData.getSchemaName();
		if (!schemaString.trim().isEmpty() && Boolean.TRUE.equals(queryBuilderDao.schemaExists(schemaString))) {
			if (Boolean.TRUE.equals(queryBuilderDao.validateTableExists(filterData.getTableName(), schemaString))) {
				if (queryBuilderDao.validateColumnsExist(filterData.getColumnName(), filterData.getTableName(),
						schemaString)) {
					queryResponsePojo.setIsSuccess(true);
				} else {
					queryResponsePojo.setIsSuccess(false);
					queryResponsePojo.setMessage("Enter Valid Column for the tables");
				}
			} else {
				queryResponsePojo.setIsSuccess(false);
				queryResponsePojo.setMessage("Enter Valid table of the Schema");
			}
		} else {
			queryResponsePojo.setIsSuccess(false);
			queryResponsePojo.setMessage("Enter the Valid Schema Name");
		}
		return queryResponsePojo;
	}

}
