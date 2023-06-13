package com.tm.querybuilder.controller;

import java.util.HashMap;
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
@RequestMapping(value = "/query")
public class QueryController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	/**
	 * Before Buildling query In this API it will validate the schema exist for the
	 * schema the table and its column should be match the it allow to build query
	 * depend on the request select query with and without where clause.
	 * 
	 * @param BuilderRequestPojo
	 * @return QueryResponsePojo
	 */
	@PostMapping("/fetchQuery")
	public QueryResponsePojo fetchQuery(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			String schemaString = filterData.getSchemaName();
			if (Boolean.TRUE.equals(queryBuilderService.isSchemaExist(schemaString))
					&& !schemaString.trim().isEmpty()) {
				if (Boolean.TRUE.equals(queryBuilderService.isValidColumns(filterData.getColumnNames(),
						filterData.getTableName(), schemaString)
						&& queryBuilderService.isValidTable(schemaString, filterData.getTableName()))) {
					Map<String, String> responseMap = new HashMap<>();
					responseMap.put("query", queryBuilderService.fetchQuery(filterData));
					queryResponsePojo.response("Selected Data", responseMap, true);
				} else {
					queryResponsePojo.response("Not a Valid column or table", null, false);
				}
			} else {
				queryResponsePojo.response(Constants.VALID_TABLE, null, false);
			}
		} catch (Exception exception) {
			queryResponsePojo.response(Constants.BAD_REQUEST, exception.getMessage(), false);
		}
		return queryResponsePojo;
	}

}
