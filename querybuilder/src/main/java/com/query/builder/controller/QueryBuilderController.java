package com.query.builder.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.response.QueryResponsePojo;
import com.query.builder.service.QueryBuilderService;

@CrossOrigin
@RestController
@RequestMapping(value = "/query")
public class QueryBuilderController {

	@Autowired
	private QueryBuilderService queryBuilderService;

	/**
	 * Get the entire ColumnAndTableName using database Name
	 */
	@PostMapping("/getTableColumn")
	public QueryResponsePojo getTableColumn(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		try {
			String dataBase = builderRequestPojo.getDataBase();
			if (!dataBase.trim().isEmpty()) {
				Map<String, Map<String, String>> response = queryBuilderService.getTableColumn(builderRequestPojo);
				queryResponsePojo.response("Entire table Details of the Database", response, true);
			} else {
				queryResponsePojo.response("Enter DataBaseName value", "is Empty", false);
			}
		} catch (NullPointerException e) {
			queryResponsePojo.response("Enter the Database", e.getMessage(), false);
		}
		return queryResponsePojo;
	}

	/**
	 * .This Api is filter condition of the selected columns for the tables
	 */
	@PostMapping("/getFilterData")
	public QueryResponsePojo getFilterData(@Valid @RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		FilterData filterData = builderRequestPojo.getRequestData();
		LinkedList<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
		if (filterData != null) {
			Map<String, String> query = queryBuilderService.getFilterQuery(builderRequestPojo);
			Map<String, Object> response = queryBuilderService.getFilterData(query);
			Object value = response.get("filterResponse");
			if (value instanceof List) {
				List<?> listValue = (List<?>) value;
				if (listValue.isEmpty()) {
					queryResponsePojo.response("No data found", null, false);
				} else {
					queryResponsePojo.response("Selected table details", response, true);
				}
			}
		} else if (joinDatas != null) {
			Map<String, String> query = queryBuilderService.getJoinQuery(builderRequestPojo);
			Map<String, Object> response = queryBuilderService.getJoinData(query);

			queryResponsePojo.response("Join Data", response, true);
		} else {
			queryResponsePojo.response("Field cannot be blank", null, false);
		}
		return queryResponsePojo;

	}

	@PostMapping("/getFilterQuery")
	public QueryResponsePojo getFilterQuery(@RequestBody BuilderRequestPojo builderRequestPojo) {
		QueryResponsePojo queryResponsePojo = new QueryResponsePojo();
		FilterData filterData = builderRequestPojo.getRequestData();
		LinkedList<JoinData> joinDatas = builderRequestPojo.getJoinDatas();
		if (filterData != null) {
			Map<String, String> response = queryBuilderService.getFilterQuery(builderRequestPojo);
			queryResponsePojo.response("Selected Data", response, true);
		} else if (joinDatas != null) {
			Map<String, String> response = queryBuilderService.getJoinQuery(builderRequestPojo);
			queryResponsePojo.response("Join Data", response, true);
		} else {
			queryResponsePojo.response("Field cannot be blank", null, false);
		}
		return queryResponsePojo;
	}

}
