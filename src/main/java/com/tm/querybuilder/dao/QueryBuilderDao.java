package com.tm.querybuilder.dao;

import java.util.List;
import java.util.Map;

import com.tm.querybuilder.dto.FilterData;

public interface QueryBuilderDao {

	// This method will return the column And TableName of the database
	public Map<String, Map<String, String>> getTableColumn(String schemaName);

	// This Api for dynamic join query for multiple tables
	public Map<String, Object> getQueryExecution(Map<String, String> query);

	boolean schemaExists(String schemaName);

	boolean checkTablesExistInSchema(String schemaName);

	Map<String, Map<String, String>> getDataType(FilterData filterData);

	StringBuilder whereCondition(FilterData filterData);

	boolean validateTableExists(String tableName, String schemaName);

	boolean validateColumnsExist(List<String> columns, String tableName, String schemaName);


}
