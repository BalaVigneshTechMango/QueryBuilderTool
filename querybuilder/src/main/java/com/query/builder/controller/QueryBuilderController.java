package com.query.builder.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
	public QueryResponsePojo findSchema(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>> response = queryBuilderService.getTableNames(builderRequestPojo);
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
	public QueryResponsePojo getColumnName(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<String> response = queryBuilderService.getColumnName(builderRequestPojo);
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
		List<Map<String, Object>> response = queryBuilderService.groupBy(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage("Entire column of table");
		return queryResponsePojo;
	}

	/**
	 * @return get the entire ColumnAndTableName using database Name
	 */
	@PostMapping("/getColumnAndTableName")
	public QueryResponsePojo getColumnAndTableName(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>> response = queryBuilderService.getColumnAndTableName(builderRequestPojo);
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
	public QueryResponsePojo getTableValues(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		Map<String, String> response = queryBuilderService.getColumnAndDatatypes(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage(" ColumnName and its DataType of particular table");
		return queryResponsePojo;
	}

	/**
	 * Get the column and data(values) using table name and schemaName(db Name)
	 */
	@PostMapping("/getColumnValues")
	public QueryResponsePojo getColumnAndValues(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>> response = queryBuilderService.getColumnValues(builderRequestPojo);
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
		queryResponsePojo.setMessage(" Get List ColumnName And values of the table");
		return queryResponsePojo;
	}

	/**
	 * get the table column by datatype (using the datatype and tableName)
	 */
	@PostMapping("/getTableDataByType")
	public QueryResponsePojo getTableDataByType(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>> response = queryBuilderService.getTableDataByType(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage(" ColumnName And values of the table");
		return queryResponsePojo;

	}

	// get the Current database Name from project
	@PostMapping("/getDatabaseName")
	public QueryResponsePojo getDataBaseName() {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<String> response = queryBuilderService.getDataBaseName();
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage("Current database Name");
		return queryResponsePojo;

	}

	/**
	 * This Api is filter condition of the selected columns for the tables
	 */
	@PostMapping("/intFilterCondition")
	public QueryResponsePojo intFilterCondition(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		Object response = queryBuilderService.intFilterCondition(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage("int Filter Condition");
		return queryResponsePojo;

	}

	/**
	 * This Api for dynamic join query for multiple tables
	 * 
	 * @throws JsonProcessingException
	 */
	@PostMapping("/getJoinData")
	public QueryResponsePojo getJoinData(@RequestBody BuilderRequestPojo builderRequestPojo)
			throws JsonProcessingException {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>> response = queryBuilderService.getJoinData(builderRequestPojo);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
		String json = gson.toJson(response);	
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setObject(response);
		queryResponsePojo.setMessage(mapper.writeValueAsString(json));
		return queryResponsePojo;

	}

	@PostMapping("/getColumnValueDatatype")
	public QueryResponsePojo getColumnValueDatatype(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>>response= queryBuilderService.getColumnValueDatatype(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setMessage(null);
		queryResponsePojo.setObject(response);
		return queryResponsePojo;
	}
	
	@PostMapping("/getExecuteDynamicQuery")//not in use
	public QueryResponsePojo executeDynamicQuery(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		List<Map<String, Object>>response= queryBuilderService.executeDynamicQuery(builderRequestPojo);
		queryResponsePojo.setIstrue(true);
		queryResponsePojo.setMessage("get Joins");
		queryResponsePojo.setObject(response);
		return queryResponsePojo;
	}
	

}
