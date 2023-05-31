package com.query.builder.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.dto.FilterData;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	// 3 This method will return the column And TableName of the database
	@Override
	public Map<String, Map<String, String>> getTableColumn(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getSchemaName();
		return queryBuilderDao.getTableColumn(schemaName);
	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public Map<String, Object> getFilterData(Map<String, String> query) {
		return queryBuilderDao.getFilterData(query);

	}

	@Override
	public Map<String, String> getFilterQuery(BuilderRequestPojo builderRequestPojo) {
		FilterData filterData=builderRequestPojo.getRequestData();
		return queryBuilderDao.getFilterQuery(filterData);
	}

	// This Api for dynamic join query for multiple tables
	@Override
	public Map<String, Object> getJoinData(Map<String, String> query) {

		return queryBuilderDao.getJoinedData(query);
	}

	@Override
	public Map<String, String> getJoinQuery(BuilderRequestPojo builderRequestPojo) {
		return queryBuilderDao.getJoinQuery(builderRequestPojo);
	}

	@Override
	public Boolean schemaExists(String schemaName) {
		
		return queryBuilderDao.schemaExists(schemaName);
	}

}
