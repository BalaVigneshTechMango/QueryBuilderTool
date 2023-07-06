package com.tm.querybuilder.pojo.request;

import javax.validation.constraints.NotBlank;

public class SchemaRequestPOJO {

	@NotBlank(message = "Enter the SchemaName")
	private String schemaName;

	@NotBlank(message = "Enter database Name")
	private String database;

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
