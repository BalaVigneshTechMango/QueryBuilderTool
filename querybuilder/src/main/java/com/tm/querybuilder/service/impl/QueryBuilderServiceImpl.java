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
		String schemaNameString = builderRequestPojo.getSchemaName();
		return queryBuilderDao.getTableColumn(schemaNameString);
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
		String columnString = String.join(",", filterData.getColumnNames());
		String table = filterData.getTableName();
		if (filterData.getWhereData() != null) {
			StringBuilder whereBuilder = queryBuilderDao.whereCondition(filterData);
			String sql = "Select " + columnString + " From " + schemaString + "." + table + " Where " + whereBuilder;
			previewQueryMap.put(QUERY, sql);
		} else {
			String sql = "SELECT " + columnString + " FROM " + schemaString + "." + table;
			previewQueryMap.put(QUERY, sql);
		}
		return previewQueryMap;

	}

	// This method will check the data base name and table exist in dao.
	@Override
	public QueryResponsePojo schemaCheck(String schemaString, String databaseString) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		
		if (!schemaString.trim().isEmpty() && !databaseString.trim().isEmpty()) {
			Boolean schemaExist = queryBuilderDao.schemaExists(schemaString);
			if (Boolean.TRUE.equals(schemaExist)) {
				queryBuilderDao.checkTablesExistInSchema(schemaString);
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

	// This method will check the schema and table and  column in dao.
	@Override
	public QueryResponsePojo schemaTableColumn(FilterData filterData) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		String schemaString = filterData.getSchemaName();
		if (!schemaString.trim().isEmpty() && Boolean.TRUE.equals(queryBuilderDao.schemaExists(schemaString))) {
			if (Boolean.TRUE.equals(queryBuilderDao.validateTableExists(filterData.getTableName(), schemaString))) {
				if (queryBuilderDao.validateColumnsExist(filterData.getColumnNames(), filterData.getTableName(),
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
