package com.query.builder.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.query.builder.common.MyObject;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.response.QueryResponsePojo;
import com.query.builder.service.QueryBuilderService;

@RestController
@RequestMapping(value = "/query")
public class QueryBuilderController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	/**
	 * @return It will return the entire table list in the database.
	 */
	@PostMapping("/getTableUsingSchema")
	public QueryResponsePojo findSchema() {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<String> response = queryBuilderService.getTableNames();
		queryResponsePojo.setObject(response);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setMessage("Mall project table List");
		return queryResponsePojo;
	}

	/**
	 * @return Get the list of columnName using table name db name
	 * 
	 */
	@PostMapping("/getColumnName")
	public QueryResponsePojo getColumnName() {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<String> response = queryBuilderService.getColumnName();
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage("Entire column of table");
		return queryResponsePojo;
	}

	/**
	 * This api for to create group by and using list of column
	 */
	@PostMapping("/groupBy")
	public QueryResponsePojo groupBy(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<MyObject> response = queryBuilderService.groupBy(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage("Entire column of table");
		return queryResponsePojo;
	}

	/**
	 * @return get the entire ColumnAndTableName using database Name
	 */
	@PostMapping("/getColumnAndTableName")
	public QueryResponsePojo getColumnAndTableName() {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		Object response = queryBuilderService.getColumnAndTableName();
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage("Entire column And TableName of the database");
		return queryResponsePojo;
	}

	/**
	 * Get All Schemas using schemata (get db names)
	 */
	@PostMapping("/getAllSchema")
	public QueryResponsePojo getAllSchema() {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<String> response = queryBuilderService.getAllSchema();
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage("Entire Database Name(or) Schemas ");
		return queryResponsePojo;
	}

	/**
	 * Get Column and datatype of specific table (column of the table with datatype)
	 */
	@PostMapping("/getColumnAndDatatype")
	public QueryResponsePojo getTableValues() {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		Map<String, String> response = queryBuilderService.getColumnAndDatatypes();
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage(" ColumnName and its DataType of particular table");
		return queryResponsePojo;
	}

	/**
	 * Get the column and data(values) using table name and schemaName(db Name)
	 */
	@PostMapping("/getColumnValues")
	public QueryResponsePojo getColumnAndValues() {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>> response = queryBuilderService.getColumnValues();
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage(" ColumnName And values of the table");
		return queryResponsePojo;
	}

	/**
	 * get Column and values of by using list of table Name
	 */
	@PostMapping("getColumnDataListOfTable")
	public QueryResponsePojo getColumValuesListOfTables(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>> response = queryBuilderService.getColumnValueListOfTable(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage(" ColumnName And values of the table");
		return queryResponsePojo;
	}
	

}
