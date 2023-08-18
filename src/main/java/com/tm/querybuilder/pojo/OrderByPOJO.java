package com.tm.querybuilder.pojo;

import javax.validation.constraints.NotBlank;

public class OrderByPOJO {

	@NotBlank(message = "Enter the Order Column Name")
	private String orderColumnName;
	@NotBlank(message = "Enter the Order Type")
	private String orderType;

	public String getOrderColumnName() {
		return orderColumnName;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderColumnName(String orderColumnName) {
		this.orderColumnName = orderColumnName;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
