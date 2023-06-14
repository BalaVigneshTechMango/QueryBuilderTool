package com.tm.querybuilder.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tm.querybuilder.constant.MessageConstants;
import com.tm.querybuilder.request.SchemaPOJO;
import com.tm.querybuilder.response.QueryBuilderResponsePOJO;
import com.tm.querybuilder.service.QueryBuilderService;

@CrossOrigin
@RestController
@RequestMapping(value = "/columndetails")
public class ColumnDetailController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ColumnDetailController.class);

	/**
	 * By using schema name get the list of tables and column and its datatype. In
	 * this API it will check the schema is valid and for the schema it should have
	 * atleast one Table in the schema to get the details of table.
	 * 
	 * @param schemaPojo
	 * @return QueryBuilderResponsePOJO
	 */
	@PostMapping("/fetchColumnDetails")
	public QueryBuilderResponsePOJO fetchColumnDetails(@Valid @RequestBody SchemaPOJO schemaPojo) {
		LOGGER.info("fetch Column Details Api:");
		QueryBuilderResponsePOJO queryBuilderResponsePojo = new QueryBuilderResponsePOJO();
		try {
			LOGGER.info(schemaPojo.getSchemaName());
			if (Boolean.TRUE.equals(queryBuilderService.isSchemaExist(schemaPojo.getSchemaName()))) {
				LOGGER.info(MessageConstants.SCHEMA_IS_VALID);
				queryBuilderResponsePojo.response("Table Details of the Schema",
						queryBuilderService.fetchColumnDetails(schemaPojo.getSchemaName()), true);
			} else {
				LOGGER.error(MessageConstants.NOT_VALID_SCHEMA, schemaPojo.getSchemaName());
				queryBuilderResponsePojo.response(MessageConstants.NOT_VALID_SCHEMA, false);
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage());
			queryBuilderResponsePojo.errorResponse(MessageConstants.BAD_REQUEST);
		}
		return queryBuilderResponsePojo;
	}

}
