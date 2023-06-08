package com.tm.querybuilder.request;


import javax.validation.Valid;

import com.tm.querybuilder.dto.FilterData;

public class BuilderRequestPojo {


	private String schemaName;

	private String database;

	@Valid
	private FilterData requestData;


	public FilterData getRequestData() {
		return requestData;
	}

	public void setRequestData(FilterData requestData) {
		this.requestData = requestData;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

}
