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

import com.tm.querybuilder.constant.Constants;
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
	 * @param builderRequestPojo
	 * @return QueryResponsePojo
	 */
	@PostMapping("/fetchResultData")
	public QueryResponsePojo fetchResultData(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		Map<String, Object> responseMap = new HashMap<>();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			String schemaString = filterData.getSchemaName();
			if (Boolean.TRUE.equals(queryBuilderService.isSchemaExist(schemaString))
					&& !schemaString.trim().isEmpty()) {
				if (Boolean.TRUE.equals(queryBuilderService.isValidateColumns(filterData.getColumnNames(),
						filterData.getTableName(), schemaString)
						&& queryBuilderService.isValidateTable(schemaString, filterData.getTableName()))) {
					List<Map<String, Object>> responseList = queryBuilderService
							.fetchResultData(queryBuilderService.fetchQuery(filterData));
					responseMap.put("filterResponse", responseList);
					if (responseMap.toString().trim().equals("[]")) {
						queryResponsePojo.response(Constants.NO_DATA, null, false);
					} else {
						queryResponsePojo.response("Selected table details", responseMap, true);
					}
				} else {
					queryResponsePojo.response("Not a Valid column or table", null, false);
				}
			} else {
				queryResponsePojo.response(Constants.SCHEMA_EMPTY, null, false);
			}
		} catch (Exception exception) {
			queryResponsePojo.response(Constants.BAD_REQUEST, exception.getMessage(), false);
		}
		return queryResponsePojo;
	}

}
