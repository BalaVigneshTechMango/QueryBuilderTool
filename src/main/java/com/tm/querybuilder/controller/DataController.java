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
@RequestMapping(value = "/data")
public class DataController {

	@Autowired
	private QueryBuilderService queryBuilderService;
	
	/**
	 * Before Execution In this API it will validate the schema exist for the schema
	 * the table and its column should be match the it allow to build query by using
	 * build method and the by using this it will execute depend on the request
	 * select query with and without where clause.
	 */
	@PostMapping("/fetchResultData")
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

	
}
