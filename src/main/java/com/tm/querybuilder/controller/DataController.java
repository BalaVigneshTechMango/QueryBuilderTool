package com.tm.querybuilder.controller;

import java.util.HashMap;
import java.util.List;
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
	public QueryResponsePojo fetchResultData(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		Map<String, Object> response = new HashMap<>();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			String schemaString = filterData.getSchemaName();
			if (queryBuilderService.schemaExistDetails(schemaString) > 0 && !schemaString.trim().isEmpty()) {
				if (Boolean.TRUE.equals(queryBuilderService.validateColumns(filterData.getColumnNames(),
						filterData.getTableName(), schemaString)
						&& queryBuilderService.validateTable(schemaString, filterData.getTableName()))) {
					List<Map<String, Object>> responseList = queryBuilderService
							.fetchResultData(queryBuilderService.fetchQuery(filterData));
					response.put("filterResponse", responseList);
					if (response.toString().trim().equals("[]")) {
						queryResponsePojo.response("No data found", null, false);
					} else {
						queryResponsePojo.response("Selected table details", response, true);
					}
				} else {
					queryResponsePojo.response("Not a Valid column or table", null, false);
				}
			} else {
				queryResponsePojo.response("enter valid", null, false);
			}
		} catch (Exception exception) {
			queryResponsePojo.response("Bad Request", exception.getMessage(), false);
		}
		return queryResponsePojo;
	}

}
