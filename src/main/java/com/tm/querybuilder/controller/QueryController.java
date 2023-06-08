package com.tm.querybuilder.controller;

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
public class QueryController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	/**
	 * Before Buildling query In this API it will validate the schema exist for the
	 * schema the table and its column should be match the it allow to build query
	 * depend on the request select query with and without where clause.
	 */
	@PostMapping("/fetchQuery")
	public QueryResponsePojo fetchQuery(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			FilterData filterData = builderRequestPojo.getRequestData();
			QueryResponsePojo responseValidPojo = queryBuilderService.schemaDetailsExist(filterData.getSchemaName(),
					filterData.getTableName(), filterData.getColumnName());
			if (Boolean.TRUE.equals(responseValidPojo.getIsSuccess())) {
				queryResponsePojo.response("Selected Data", queryBuilderService.fetchQuery(filterData), true);
			} else {
				queryResponsePojo.response(responseValidPojo.getMessage(), null, responseValidPojo.getIsSuccess());
			}
		} catch (Exception exception) {
			queryResponsePojo.response("Bad Request", exception.getMessage(), false);
		}
		return queryResponsePojo;
	}

}
