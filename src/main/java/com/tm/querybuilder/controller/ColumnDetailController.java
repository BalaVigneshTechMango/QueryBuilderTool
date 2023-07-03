package com.tm.querybuilder.controller;

import java.util.ArrayList;
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
import com.tm.querybuilder.dto.TableDetailPojo;
import com.tm.querybuilder.enums.KeyTypes;
import com.tm.querybuilder.dto.ColumnDetails;
import com.tm.querybuilder.dto.ForeignKeys;
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
		LOGGER.info("fetch TableDetailPojo Details Api:");
		QueryBuilderResponsePOJO queryBuilderResponsePojo = new QueryBuilderResponsePOJO();
		try {
			if (Boolean.TRUE.equals(queryBuilderService.isSchemaExist(schemaPojo.getSchemaName()))) {
				LOGGER.info("schema is valid and fetching the details for tables");

				List<ColumnDetails> columnDetailList = queryBuilderService
						.fetchColumnDetails(schemaPojo.getSchemaName());
				Map<String, TableDetailPojo> tablesMap = new HashMap<>();

				for (ColumnDetails columnDetails : columnDetailList) {

					TableDetailPojo tableDetailPojo = new TableDetailPojo();
					if (tablesMap.containsKey(columnDetails.getTableName())) {
						tableDetailPojo = tablesMap.get(columnDetails.getTableName());
						Map<String, String> column = tableDetailPojo.getColumn();
						column.put(columnDetails.getColumnName(), columnDetails.getDataType());
					} else {
						Map<String, String> column = new HashMap<>();
						column.put(columnDetails.getColumnName(), columnDetails.getDataType());
						tableDetailPojo.setColumn(column);
						tablesMap.put(columnDetails.getTableName(), tableDetailPojo);
					}
					if (KeyTypes.PRIMARY_KEY.getOperator().equals(columnDetails.getColumnKey())) {
						tableDetailPojo = tablesMap.get(columnDetails.getTableName());
						tableDetailPojo.setPrimarykey(columnDetails.getColumnName());
					} else if (KeyTypes.FOREGIN_KEY.getOperator().equals(columnDetails.getColumnKey())) {
						List<ForeignKeys> foreignKeyList = new ArrayList<>();
						ForeignKeys foreignKeys = new ForeignKeys();
						foreignKeys.setColumnName(columnDetails.getColumnName());
						foreignKeys.setReferencecolumn(columnDetails.getReferenceColumn());
						foreignKeys.setReferenceTable(columnDetails.getReferenceTable());
						tableDetailPojo = tablesMap.get(columnDetails.getTableName());
						foreignKeyList.add(foreignKeys);
						tableDetailPojo.setForeignKeys(foreignKeyList);
					}
				}
				queryBuilderResponsePojo.response("Table Details of the Schema", tablesMap, true);
			} else {
				LOGGER.error("Not Valid schema");
				queryBuilderResponsePojo.response(MessageConstants.NOT_VALID_SCHEMA, false);
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage());
			queryBuilderResponsePojo.errorResponse(MessageConstants.BAD_REQUEST);
		}
		return queryBuilderResponsePojo;
	}

}
