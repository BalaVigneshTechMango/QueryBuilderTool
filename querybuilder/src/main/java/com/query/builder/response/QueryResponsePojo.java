package com.query.builder.response;

public class QueryResponsePojo {

	private String message;
	private Object object;
	private Boolean istrue;

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
