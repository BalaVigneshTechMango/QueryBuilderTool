package com.query.builder.service.impl;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	// 1.get All table Name in Database
	@Override
	public List<Map<String, Object>> getTableNames(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getDataBase();
		return queryBuilderDao.getTableNames(schemaName);
	}

	// 2. Get the list list of table name and response it back as table wise select
	// Query
	@Override
	public List<Map<String, Object>> listOfSelectQuery(BuilderRequestPojo builderRequestPojo) {
		List<String> tableName = builderRequestPojo.getListTableName();
		String schemaName = builderRequestPojo.getDataBase();
		return queryBuilderDao.listOfSelectQuery(tableName, schemaName);
	}

	// 3 This method will return the column And TableName of the database
	@Override
	public Map<String, Map<String, String>> getColumnAndTableName(BuilderRequestPojo builderRequestPojo) {
		String dataBase = builderRequestPojo.getDataBase();
		return queryBuilderDao.getColumnAndTableName(dataBase);
	}

	@Override
	public List<Map<String, Object>> getColumnListOfTableName(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getDataBase();
		List<String> listTableName = builderRequestPojo.getListTableName();
		return queryBuilderDao.getColumnListOfTableName(schemaName, listTableName);
	}

	@Override
	public List<Map<String, Object>> groupBy(BuilderRequestPojo builderRequestPojo) {
		List<String> columnNames = builderRequestPojo.getColumnNames();
		String schemaName = builderRequestPojo.getDataBase();
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.groupBy(columnNames, schemaName, tableName);
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
		String schemaName = builderRequestPojo.getDataBase();
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.getColumnValues(schemaName, tableName);
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

		return queryBuilderDao.filterCondition(builderRequestPojo);

	}

	// This Api for dynamic join query for multiple tables
	@Override
	public List<Map<String, Object>> getJoinData(BuilderRequestPojo builderRequestPojo) {

		return queryBuilderDao.getJoinedData(builderRequestPojo);
	}

	@Override
	public List<Map<String, Object>> getColumnValueDatatype(BuilderRequestPojo builderRequestPojo) {
		List<String> listTableName = builderRequestPojo.getListTableName();
		String schemaName = builderRequestPojo.getDataBase();
		return queryBuilderDao.getColumnValueDatatype(listTableName, schemaName);
	}

	// 6.This API is used to join the table with using inner join without on
	// condition and where conditon
	@Override
	public List<Map<String, Object>> innerJoin(BuilderRequestPojo builderRequestPojo) {
		List<String> tableName = builderRequestPojo.getListTableName();
		return queryBuilderDao.innerJoin(tableName);
	}

	// not in use

	// get All column name in particular table
	@Override
	public List<String> getColumnName(BuilderRequestPojo builderRequestPojo) {
		String schemaName = builderRequestPojo.getDataBase();
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.getColumnName(schemaName, tableName);

	}

	@Override
	public List<Map<String, Object>> executeDynamicQuery(BuilderRequestPojo builderRequestPojo) {
		List<String> tables = builderRequestPojo.getListTableName();
		List<String> joins = builderRequestPojo.getJoins();
		String whereCondition = builderRequestPojo.getWhereCondition();
		return queryBuilderDao.executeDynamicQuery(tables, joins, whereCondition);
	}

	// Get All Schemas using schemata (get db names)
	@Override
	public List<String> getAllSchema() {
		return queryBuilderDao.getAllSchemas();
	}

	@Override
	public List<String> getPrimaryKeyAndIndexColumns(BuilderRequestPojo builderRequestPojo) {
		String database = builderRequestPojo.getDataBase();
		String tableName = builderRequestPojo.getTableName();
		return queryBuilderDao.getPrimaryKeyAndIndexColumns(database, tableName);
	}

	@Override
	public List<Map<String, Object>> getJoinQuery(BuilderRequestPojo builderRequestPojo) {
		return queryBuilderDao.getJoinQuery(builderRequestPojo);
	}

	@Override
	public List<Map<String, Object>> getFilterQuery(BuilderRequestPojo builderRequestPojo) {
		return queryBuilderDao.getFilterQuery(builderRequestPojo);
	}

}
