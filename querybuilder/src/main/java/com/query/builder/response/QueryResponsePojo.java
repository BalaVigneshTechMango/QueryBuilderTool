package com.query.builder.response;


public class QueryResponsePojo {

	private String message;
	private Object responseData;
	private Boolean isSuccess;

	public String getMessage() {
		return message;
	}

	public Object getResponseData() {
		return responseData;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setResponseData(Object object) {
		this.responseData = object;
	}

	public void setIsSuccess(Boolean istrue) {
		this.isSuccess = istrue;
	}

}
