package com.tm.querybuilder.controller;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.request.BuilderRequestPojo;
import com.tm.querybuilder.response.QueryResponsePojo;
import com.tm.querybuilder.service.QueryBuilderService;

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
			String database=builderRequestPojo.getDatabase();
			QueryResponsePojo queryResponseValid = queryBuilderService.schemaCheck(schemaName,database);
			if (Boolean.TRUE.equals(queryResponseValid.getIsSuccess())) {
				Map<String, Map<String, String>> response = queryBuilderService.getTableColumn(builderRequestPojo);
				queryResponsePojo.response("Table Details of the Schema", response, true);
			} else {
				queryResponsePojo.response(queryResponseValid.getMessage(), null, queryResponseValid.getIsSuccess());
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
	@PostMapping("/getQueryExecution")
	public QueryResponsePojo getQueryExecution(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			QueryResponsePojo queryResponseValid = queryBuilderService.schemaTableColumn(filterData);
			if (Boolean.TRUE.equals(queryResponseValid.getIsSuccess())) {
				Map<String, String> query = queryBuilderService.getQueryBuild(builderRequestPojo);
				Map<String, Object> response = queryBuilderService.getQueryExecution(query);
				Object data = response.get("filterResponse");
				if (data.toString().trim().equals("[]")) {
					queryResponsePojo.response("No data found", null, false);
				} else {
					queryResponsePojo.response("Selected table details", response, true);
				}
			} else {
				queryResponsePojo.response(queryResponseValid.getMessage(), null, queryResponseValid.getIsSuccess());
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
	@PostMapping("/getQueryBuild")
	public QueryResponsePojo getQueryBuild(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			QueryResponsePojo queryResponseValid = queryBuilderService.schemaTableColumn(filterData);
			if (Boolean.TRUE.equals(queryResponseValid.getIsSuccess())) {
				Map<String, String> response = queryBuilderService.getQueryBuild(builderRequestPojo);
				queryResponsePojo.response("Selected Data", response, true);
			} else {
				queryResponsePojo.response(queryResponseValid.getMessage(), null, queryResponseValid.getIsSuccess());
			}
		} catch (Exception e) {
			queryResponsePojo.response("Bad Request", e.getMessage(), false);
		}
		return queryResponsePojo;
	}

}
