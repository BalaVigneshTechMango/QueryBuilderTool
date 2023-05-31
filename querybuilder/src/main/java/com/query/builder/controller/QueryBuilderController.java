package com.query.builder.controller;

import java.util.LinkedList;
import java.util.List;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.response.QueryResponsePojo;
import com.query.builder.service.QueryBuilderService;

@CrossOrigin
@RestController
@RequestMapping(value = "/query")
public class QueryBuilderController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	/**
	 * Get the entire ColumnAndTableName using database Name
	 */
	@PostMapping("/getTableColumn")
	public QueryResponsePojo getTableColumn(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			String schemaName = builderRequestPojo.getSchemaName();
			if (schemaName.trim().isEmpty()) {
				queryResponsePojo.response("Enter the SchemaName", "Empty Schema", false);
			} else {
				Boolean schemaExist = queryBuilderService.schemaExists(schemaName);
				if (Boolean.TRUE.equals(schemaExist)) {
					Map<String, Map<String, String>> response = queryBuilderService.getTableColumn(builderRequestPojo);
					Map<String, String> tableNameMap = response.get("tableName");
					String tableNamesValue = tableNameMap.get("tableNames");
					if (!tableNamesValue.isBlank()) {
						queryResponsePojo.response("Table Details of the Schema", response, true);
					} else {
						queryResponsePojo.response("No table in this Schema", "No table", false);
					}
				} else {
					queryResponsePojo.response("Enter SchemaName Properly ", "No Schema Found", false);
				}
			}
		} catch (Exception e) {
			queryResponsePojo.response("Bad Requests", e.getMessage(), false);
		}
		return queryResponsePojo;
	}

	/**
	 * This Api is to get the query for filterQuery api and get executed based on
	 * the request.
	 */
	@PostMapping("/getFilterData")
	public QueryResponsePojo getFilterData(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			LinkedList<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
			if (filterData != null) {
				Map<String, String> query = queryBuilderService.getFilterQuery(builderRequestPojo);
				Map<String, Object> response = queryBuilderService.getFilterData(query);
				Object data = response.get("filterResponse");
				String responseData = data.toString();
				if (responseData.isBlank()) {
					queryResponsePojo.response("No data found", null, false);
				} else {
					queryResponsePojo.response("Selected table details", response, true);
				}
			} else if (joinDatas != null) {
				Map<String, String> query = queryBuilderService.getJoinQuery(builderRequestPojo);
				Map<String, Object> response = queryBuilderService.getJoinData(query);
				queryResponsePojo.response("Join Data", response, true);
			} else {
				queryResponsePojo.response("Field cannot be blank", "Bad Request", false);
			}
		} catch (Exception e) {
			queryResponsePojo.response("Bad Request", e.getMessage(), false);
		}
		return queryResponsePojo;
	}

	/**
	 * This api will get the query for the join or select query with and without
	 * where clause
	 */
	@PostMapping("/getFilterQuery")
	public QueryResponsePojo getFilterQuery(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			LinkedList<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
			String schemaName = builderRequestPojo.getRequestData().getSchemaName();
			Boolean schemaExist = queryBuilderService.schemaExists(schemaName);
			if (Boolean.TRUE.equals(schemaExist)) {
				if (filterData != null) {
					Map<String, String> response = queryBuilderService.getFilterQuery(builderRequestPojo);
					queryResponsePojo.response("Selected Data", response, true);
				} else if (joinDatas != null) {
					Map<String, String> response = queryBuilderService.getJoinQuery(builderRequestPojo);
					queryResponsePojo.response("Join Data", response, true);
				} else {
					queryResponsePojo.response("Field cannot be blank", null, false);
				}
			} else if (schemaName.trim().isEmpty()) {
				queryResponsePojo.response("Enter SchemaName Name ", "Enter validate Schema", false);
			} else {
				queryResponsePojo.response("Enter SchemaName Properly ", "No Schema Found", false);
			}
		} catch (Exception e) {
			queryResponsePojo.response("Bad Request", e.getMessage(), false);
		}
		return queryResponsePojo;

	}
}
