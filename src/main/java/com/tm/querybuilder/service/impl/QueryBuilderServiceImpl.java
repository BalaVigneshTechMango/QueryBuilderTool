package com.tm.querybuilder.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tm.querybuilder.dao.QueryBuilderDao;
import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.response.QueryResponsePojo;
import com.tm.querybuilder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	// The method get request from the BuilderRequestPojo to get the details of
	// table and columm by using schema name get the details with dao layer.
	@Override
	public Map<String, Map<String, String>> fetchColumnDetails(String schemaName) {

		return queryBuilderDao.fetchColumnDetails(schemaName);

	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public Map<String, Object> fetchResultData(String queryString) {
		return queryBuilderDao.fetchResultData(queryString);

	}

	// In this method get request from pojo based on the request this method will
	// send data to dao layer. select query with and without where caluse
	@Override
	public String fetchQuery(FilterData filterData) {

		String schemaString = filterData.getSchemaName();
		String columnString = String.join(",", filterData.getColumnName());
		String tableString = filterData.getTableName();
		// select query with where clause of single table
		if (filterData.getWhereData() != null) {
			return "Select " + columnString + " From " + schemaString + "." + tableString + " Where "
					+ queryBuilderDao.whereCondition(filterData);
		} else {
			// select query without where clause of single table
			return "SELECT " + columnString + " FROM " + schemaString + "." + tableString;

		}

	}

	// This method will check the data base name and table exist in dao.
	@Override
	public QueryResponsePojo schemaExistDetails(String schemaNameString, String databaseString) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		if (!schemaNameString.trim().isEmpty() && !databaseString.trim().isEmpty()) {
			Integer schemaExistInt = queryBuilderDao.schemaExistDetails(schemaNameString);
			if (schemaExistInt > 0) {
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
	public QueryResponsePojo schemaDetailsExist(String schemaString, String tableName, List<String> columnList) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();

		if (!schemaString.trim().isEmpty() && queryBuilderDao.schemaExistDetails(schemaString) > 0) {
			if (Boolean.TRUE.equals(queryBuilderDao.validateTable(tableName, schemaString))) {
				if (queryBuilderDao.validateColumns(columnList, tableName, schemaString)) {
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
