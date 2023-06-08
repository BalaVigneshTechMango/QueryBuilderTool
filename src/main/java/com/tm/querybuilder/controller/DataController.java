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
	public QueryResponsePojo fetchResultData(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			QueryResponsePojo responseValidPojo = queryBuilderService.schemaDetailsExist(filterData.getSchemaName(),
					filterData.getTableName(), filterData.getColumnName());
			if (Boolean.TRUE.equals(responseValidPojo.getIsSuccess())) {
				Map<String, Object> responseMap = queryBuilderService
						.fetchResultData(queryBuilderService.fetchQuery(filterData));
				if (responseMap.get("filterResponse").toString().trim().equals("[]")) {
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

}
