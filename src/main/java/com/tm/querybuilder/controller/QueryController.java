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
public class QueryController {

	@Autowired
	private QueryBuilderService queryBuilderService;


	@PostMapping("/fetchQuery")
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
