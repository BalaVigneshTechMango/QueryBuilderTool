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
	 * By using schema name get the list of tables and column and its datatype. In
	 * this API it will check the schema is valid and for the schema it should have
	 * atleast one Table in the schema to get the details of table.
	 */
	@PostMapping("/getTableColumn")
	public QueryResponsePojo getTableColumn(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			String schemaString = builderRequestPojo.getSchemaName();
			String databaseString = builderRequestPojo.getDatabase();
			QueryResponsePojo responseValidPojo = queryBuilderService.schemaCheck(schemaString, databaseString);
			if (Boolean.TRUE.equals(responseValidPojo.getIsSuccess())) {
				Map<String, Map<String, String>> responseMap = queryBuilderService.getTableColumn(builderRequestPojo);
				queryResponsePojo.response("Table Details of the Schema", responseMap, true);
			} else {
				queryResponsePojo.response(responseValidPojo.getMessage(), null, responseValidPojo.getIsSuccess());
			}
		} catch (Exception exception) {
			queryResponsePojo.response("Bad Requests", exception.getMessage(), false);
		}
		return queryResponsePojo;
	}

	/**
	 * Before Execution In this API it will validate the schema exist for the schema
	 * the table and its column should be match the it allow to build query by using
	 * build method and the by using this it will execute depend on the request
	 * select query with and without where clause.
	 */
	@PostMapping("/getQueryExecution")
	public QueryResponsePojo getQueryExecution(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			QueryResponsePojo responseValidPojo = queryBuilderService.schemaTableColumn(filterData);
			if (Boolean.TRUE.equals(responseValidPojo.getIsSuccess())) {
				Map<String, String> queryMap = queryBuilderService.getQueryBuild(builderRequestPojo);
				Map<String, Object> responseMap = queryBuilderService.getQueryExecution(queryMap);
				Object dataObject = responseMap.get("filterResponse");
				if (dataObject.toString().trim().equals("[]")) {
					queryResponsePojo.response("No data found", null, false);
				} else {
					queryResponsePojo.response("Selected table details", responseMap, true);
				}
			} else {
				queryResponsePojo.response(responseValidPojo.getMessage(), null, responseValidPojo.getIsSuccess());
			}
		} catch (Exception exception) {
			queryResponsePojo.response("Bad Request", exception.getMessage(), false);
		}
		return queryResponsePojo;
	}

	/**
	 * Before Buildling query In this API it will validate the schema exist for the
	 * schema the table and its column should be match the it allow to build query
	 * depend on the request select query with and without where clause.
	 */
	@PostMapping("/getQueryBuild")
	public QueryResponsePojo getQueryBuild(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			QueryResponsePojo responseValidPojo = queryBuilderService.schemaTableColumn(filterData);
			if (Boolean.TRUE.equals(responseValidPojo.getIsSuccess())) {
				Map<String, String> responseMap = queryBuilderService.getQueryBuild(builderRequestPojo);
				queryResponsePojo.response("Selected Data", responseMap, true);
			} else {
				queryResponsePojo.response(responseValidPojo.getMessage(), null, responseValidPojo.getIsSuccess());
			}
		} catch (Exception exception) {
			queryResponsePojo.response("Bad Request", exception.getMessage(), false);
		}
		return queryResponsePojo;
	}

}
