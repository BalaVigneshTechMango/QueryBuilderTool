package com.query.builder.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.query.builder.common.MyObject;
import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.service.QueryBuilderService;

@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private QueryBuilderDao queryBuilderDao;

	// get All table Name in Database
	@Override
	public List<String> getTableNames() {
		return queryBuilderDao.getTableNames();
	}

	// get All column name in particular table
	@Override
	public List<String> getColumnName() {

		return queryBuilderDao.getColumnName();

	}

	@Override
	public List<MyObject> groupBy(BuilderRequestPojo builderRequestPojo) {
		List<String> columnNames = builderRequestPojo.getColumnNames();

		return queryBuilderDao.groupBy(columnNames);
	}

	// This method will return the column And TableName of the database
	@Override
	public Object getColumnAndTableName() {
		return queryBuilderDao.getColumnAndTableName();
	}

	// Get All Schemas using schemata (get db names)
	@Override
	public List<String> getAllSchema() {
		return queryBuilderDao.getAllSchemas();
	}

	// Get the specific table column of the table with datatype
	@Override
	public Map<String, String> getColumnAndDatatypes() {
		return queryBuilderDao.getColumnAndDatatypes();
	}

	//Get the column and data(values) using table name and schemaName(db Name)
	@Override
	public List<Map<String, Object>> getColumnValues() {
		return queryBuilderDao.getColumnValues();
	}

	//get Column and values of by using list of table Name
	@Override
	public List<Map<String, Object>> getColumnValueListOfTable(BuilderRequestPojo builderRequestPojo) {
		List<String>listOftableName=builderRequestPojo.getListTableName();
		return queryBuilderDao.getColumnValueListOfTable(listOftableName);
	}

}
