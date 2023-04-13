package com.query.builder.request;


public class JoinConditionDto {

	
	private String lscolumn;
	
	private String rscolumn;
	
	private String conditionType;
	

	public String getLscolumn() {
		return lscolumn;
	}

	public String getRscolumn() {
		return rscolumn;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setLscolumn(String lscolumn) {
		this.lscolumn = lscolumn;
	}

	public void setRscolumn(String rscolumn) {
		this.rscolumn = rscolumn;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	
	
}
