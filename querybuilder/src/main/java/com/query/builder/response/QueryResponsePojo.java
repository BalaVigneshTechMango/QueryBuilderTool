package com.query.builder.response;

import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;

public class QueryResponsePojo {

	private String message;
	private Object object;
	private Boolean istrue;
	private JsonObject jsonOutput;
	
	
	

	public JsonObject getJsonOutput() {
		return jsonOutput;
	}

	public void setJsonOutput(JsonObject jsonOutput) {
		this.jsonOutput = jsonOutput;
	}

	public String getMessage() {
		return message;
	}

	public Object getObject() {
		return object;
	}

	public Boolean getIstrue() {
		return istrue;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public void setIstrue(Boolean istrue) {
		this.istrue = istrue;
	}

}
