package com.tm.querybuilder.request;

import javax.validation.Valid;

import com.tm.querybuilder.dto.FilterData;

public class QueryBuilderRequestPOJO extends SchemaPOJO {

	@Valid
	private FilterData requestData;

	public FilterData getRequestData() {
		return requestData;
	}

	public void setRequestData(FilterData requestData) {
		this.requestData = requestData;
	}

}
