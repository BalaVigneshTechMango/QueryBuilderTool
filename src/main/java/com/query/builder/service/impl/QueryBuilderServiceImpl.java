package com.query.builder.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.dto.FilterData;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.response.QueryResponsePojo;
import com.query.builder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	private static final String QUERY = "query";

	// 3 This method will return the column And TableName of the database
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

	@Override
	public Map<String, String> getQueryBuild(BuilderRequestPojo builderRequestPojo) {
		
		FilterData filterData = builderRequestPojo.getRequestData();
		String schemaName = filterData.getSchemaName();
		Map<String, String> previewQuery = new HashMap<>();
		String columnName = String.join(",", filterData.getColumnNames());
		String table = filterData.getTableName();
		if (filterData.getWhereData() != null) {
			StringBuilder whereBuilder = queryBuilderDao.whereCondition(filterData);
			String sql = "Select " + columnName + " From " + schemaName + "." + table + " Where " + whereBuilder;
			previewQuery.put(QUERY, sql);
		} else {
			String sql = "SELECT " + columnName + " FROM " + schemaName + "." + table;
			previewQuery.put(QUERY, sql);
		}
		return previewQuery;

	}

	@Override
	public QueryResponsePojo schemaCheck(String schemaName,String database) {
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
