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

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public void response(String message, Object responseData, boolean isSuccess) {

	 	setMessage(message);
		setResponseData(responseData);
		setIsSuccess(isSuccess);
	}

}
