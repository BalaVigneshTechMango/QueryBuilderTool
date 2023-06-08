package com.tm.querybuilder.controller;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tm.querybuilder.request.BuilderRequestPojo;
import com.tm.querybuilder.response.QueryResponsePojo;
import com.tm.querybuilder.service.QueryBuilderService;

@CrossOrigin
@RestController
@RequestMapping(value = "/columndetails")
public class ColumnDetailController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	
	@PostMapping("/fetchColumnDetails")
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



}
