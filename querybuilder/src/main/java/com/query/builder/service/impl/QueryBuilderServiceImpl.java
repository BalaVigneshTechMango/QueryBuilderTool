package com.query.builder.service.impl;

import java.util.List;
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
		String dataBase = builderRequestPojo.getDataBase();
		return queryBuilderDao.getTableColumn(dataBase);
	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public Map<String, Object> getFilterData(BuilderRequestPojo builderRequestPojo) {

		FilterData filterDataPojo=builderRequestPojo.getFilterData();
		return queryBuilderDao.getFilterData(filterDataPojo);

	}

	@Override
	public Map<String, String> getFilterQuery(BuilderRequestPojo builderRequestPojo) {
		FilterData filterData=builderRequestPojo.getFilterData();
		return queryBuilderDao.getFilterQuery(filterData);
	}

	// This Api for dynamic join query for multiple tables
	@Override
	public List<Map<String, Object>> getJoinData(BuilderRequestPojo builderRequestPojo) {

		return queryBuilderDao.getJoinedData(builderRequestPojo);
	}

	@Override
	public List<Map<String, Object>> getJoinQuery(BuilderRequestPojo builderRequestPojo) {
		return queryBuilderDao.getJoinQuery(builderRequestPojo);
	}

}
