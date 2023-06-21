package com.tm.querybuilder.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tm.querybuilder.dto.FilterData;
import com.tm.querybuilder.request.QueryBuilderRequestPOJO;
import com.tm.querybuilder.response.QueryBuilderResponsePOJO;
import com.tm.querybuilder.service.QueryBuilderService;

@CrossOrigin
@RestController
@RequestMapping(value = "/data")
public class DataController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

	/**
	 * Before Execution In this API it will validate the schema exist for the schema
	 * the table and its column should be match the it allow to build query by using
	 * build method and the by using this it will execute depend on the request
	 * select query with and without where clause.
	 * 
	 * @param queryBuilderRequestPojo
	 * @return QueryBuilderResponsePOJO
	 */
	@PostMapping("/fetchResultData")
	public QueryBuilderResponsePOJO fetchResultData(
			@Valid @RequestBody QueryBuilderRequestPOJO queryBuilderRequestPojo) {
		LOGGER.info("fetch Result Data Api");
		QueryBuilderResponsePOJO queryBuilderResponsePojo = new QueryBuilderResponsePOJO();
		Map<String, Object> responseMap = new HashMap<>();
		try {
			FilterData filterData = queryBuilderRequestPojo.getRequestData();
			String schemaString = queryBuilderRequestPojo.getSchemaName();
			if (Boolean.TRUE.equals(queryBuilderService.isSchemaExist(schemaString))) {
				if (Boolean.TRUE.equals(queryBuilderService.isValidColumns(filterData.getColumnNames(),
						filterData.getTableName(), schemaString)
						&& queryBuilderService.isValidTable(schemaString, filterData.getTableName()))) {
					List<Map<String, Object>> responseList = queryBuilderService
							.fetchResultData(queryBuilderService.fetchQuery(filterData, schemaString));
					if (responseList.isEmpty()) {
						LOGGER.error("Result No data Found for the Request Data:");
						queryBuilderResponsePojo.response(MessageConstants.NO_DATA, false);
					} else {
						responseMap.put("filterResponse", responseList);
						queryBuilderResponsePojo.response("Data for the Request", responseMap, true);
					}
				} else {
					LOGGER.error(MessageConstants.NOT_VALID_TABLECOLUMN);
					queryBuilderResponsePojo.response(MessageConstants.NOT_VALID_TABLECOLUMN, false);
				}
			} else {
				LOGGER.error(MessageConstants.NOT_VALID_SCHEMA);
				queryBuilderResponsePojo.response(MessageConstants.NOT_VALID_SCHEMA, false);
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage());
			queryBuilderResponsePojo.errorResponse(MessageConstants.BAD_REQUEST);
		}
		return queryBuilderResponsePojo;
	}

}
