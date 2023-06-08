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
		String schemaName = builderRequestPojo.getSchemaName();
		return queryBuilderDao.getTableColumn(schemaName);
	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public Map<String, Object> getQueryExecution(Map<String, String> query) {
		return queryBuilderDao.getQueryExecution(query);

	}

	// In this method get request from pojo based on the request this method will
	// send data to dao layer. select query with and without where caluse
	@Override
	public Map<String, String> getQueryBuild(BuilderRequestPojo builderRequestPojo) {

		FilterData filterData = builderRequestPojo.getRequestData();
		String schemaName = filterData.getSchemaName();
		Map<String, String> previewQuery = new HashMap<>();
		String columnName = String.join(",", filterData.getColumnNames());
		String table = filterData.getTableName();
		// select query with where clause of single table
		if (filterData.getWhereData() != null) {
			StringBuilder whereBuilder = queryBuilderDao.whereCondition(filterData);
			String sql = "Select " + columnName + " From " + schemaName + "." + table + " Where " + whereBuilder;
			previewQuery.put(QUERY, sql);
		} else {
			// select query without where clause of single table
			String sql = "SELECT " + columnName + " FROM " + schemaName + "." + table;
			previewQuery.put(QUERY, sql);
		}
		return previewQuery;

	}

	// This method will check the data base name and table exist in dao.
	@Override
	public QueryResponsePojo schemaCheck(String schemaName, String database) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();

		if (!schemaName.trim().isEmpty() && !database.trim().isEmpty()) {
			Boolean schemaExist = queryBuilderDao.schemaExists(schemaName);
			if (Boolean.TRUE.equals(schemaExist)) {
				queryBuilderDao.checkTablesExistInSchema(schemaName);
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
		String schemaName = filterData.getSchemaName();
		if (!schemaName.trim().isEmpty() && Boolean.TRUE.equals(queryBuilderDao.schemaExists(schemaName))) {
			if (Boolean.TRUE.equals(queryBuilderDao.validateTableExists(filterData.getTableName(), schemaName))) {
				if (queryBuilderDao.validateColumnsExist(filterData.getColumnNames(), filterData.getTableName(),
						schemaName)) {
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
