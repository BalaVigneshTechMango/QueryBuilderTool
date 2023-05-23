package com.query.builder.service;

import java.util.List;
import java.util.Map;

import com.query.builder.request.BuilderRequestPojo;

public interface QueryBuilderService {

	// 3.This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getTableColumn(BuilderRequestPojo builderRequestPojo);

	// This Api is filter condition of the selected columns for the tables
	public Map<String, Object> getFilterData(BuilderRequestPojo builderRequestPojo);

	Map<String, String> getFilterQuery(BuilderRequestPojo builderRequestPojo);

	// This Api for dynamic join query for multiple tables
	List<Map<String, Object>> getJoinData(BuilderRequestPojo builderRequestPojo);

	List<Map<String, Object>> getJoinQuery(BuilderRequestPojo builderRequestPojo);


}
