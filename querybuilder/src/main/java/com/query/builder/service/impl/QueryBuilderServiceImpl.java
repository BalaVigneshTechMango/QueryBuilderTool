package com.query.builder.service.impl;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	// get All table Name in Database
	@Override
	public List<String> getTableNames(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getSchemaName();
		return queryBuilderDao.getTableNames(schemaName);
	}

	// get All column name in particular table
	@Override
	public List<String> getColumnName(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getSchemaName();
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.getColumnName(schemaName, tableName);

	}

	@Override
	public List<Map<String, Object>> groupBy(BuilderRequestPojo builderRequestPojo) {
		List<String> columnNames = builderRequestPojo.getColumnNames();
		String schemaName = builderRequestPojo.getSchemaName();
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.groupBy(columnNames, schemaName, tableName);
	}

	// This method will return the column And TableName of the database
	@Override
	public Object getColumnAndTableName(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getSchemaName();
		return queryBuilderDao.getColumnAndTableName(schemaName);
	}

	// Get All Schemas using schemata (get db names)
	@Override
	public List<String> getAllSchema() {
		return queryBuilderDao.getAllSchemas();
	}

	// Get the specific table column of the table with datatype
	@Override
	public Map<String, String> getColumnAndDatatypes(BuilderRequestPojo builderRequestPojo) {
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.getColumnAndDatatypes(tableName);
	}

	// Get the column and data(values) using table name and schemaName(db Name)
	@Override
	public List<Map<String, Object>> getColumnValues(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getSchemaName();
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.getColumnValues(schemaName, tableName);
	}

	// get Column and values of by using list of table Name
	@Override
	public List<Map<String, Object>> getColumnValueListOfTable(BuilderRequestPojo builderRequestPojo) {
		List<String> listOftableName = builderRequestPojo.getListTableName();
		String schemaName = builderRequestPojo.getSchemaName();
		return queryBuilderDao.getColumnValueListOfTable(listOftableName, schemaName);
	}

	// get the table column by datatype (using the datatype and tableName)
	@Override
	public List<Map<String, Object>> getTableDataByType(BuilderRequestPojo builderRequestPojo) {
		return queryBuilderDao.getTableDataByType(builderRequestPojo.getTableName(), builderRequestPojo.getDataType());

	}

	// get the Current database Name from project
	@Override
	public List<String> getDataBaseName() {
		return queryBuilderDao.getDatabaseName();
	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public List<Map<String, Object>> intFilterCondition(BuilderRequestPojo builderRequestPojo) {

		return queryBuilderDao.intFilterCondition(builderRequestPojo);

	}

	// This Api for dynamic join query for multiple tables
	@Override
	public List<Map<String, Object>> getJoinData(BuilderRequestPojo builderRequestPojo) {

		return queryBuilderDao.getJoinedData(builderRequestPojo);
	}

}
