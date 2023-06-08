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

	/**
	 * By using schema name get the list of tables and column and its datatype. In
	 * this API it will check the schema is valid and for the schema it should have
	 * atleast one Table in the schema to get the details of table.
	 */
	@PostMapping("/fetchColumnDetails")
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

}