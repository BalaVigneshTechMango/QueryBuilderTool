package com.query.builder.dao;

import java.util.List;
import java.util.Map;

import com.query.builder.dto.FilterData;
import com.query.builder.request.BuilderRequestPojo;

public interface QueryBuilderDao {

	// 3.This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getTableColumn(String schemaName);

	// This Api for dynamic join query for multiple tables
	public Map<String, Object> getFilterData(Map<String, String> query);
	
	public Map<String, String> getFilterQuery(FilterData filterData);

	// This Api is filter condition of the selected columns for the tables
	Map<String, Object> getJoinedData(Map<String, String> query);

	public Map<String, String> getJoinQuery(BuilderRequestPojo builderRequestPojo);


}
