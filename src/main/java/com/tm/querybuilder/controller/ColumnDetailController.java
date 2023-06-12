package com.tm.querybuilder.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tm.querybuilder.constant.Constants;
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
	 * 
	 * @param builderRequestPojo
	 * @return QueryResponsePojo
	 */
	@PostMapping("/fetchColumnDetails")
	public QueryResponsePojo fetchColumnDetails(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			String schemaString = builderRequestPojo.getSchemaName();
			if (!schemaString.trim().isEmpty()) {
				if (Boolean.TRUE.equals(queryBuilderService.isSchemaExist(schemaString))) {
					queryResponsePojo.response("Table Details of the Schema",
							queryBuilderService.fetchColumnDetails(schemaString), true);
				} else {
					queryResponsePojo.response(Constants.VALID_SCHEMA, null, false);
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
