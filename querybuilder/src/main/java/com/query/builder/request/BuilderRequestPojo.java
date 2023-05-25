package com.query.builder.request;

import java.util.LinkedList;

import javax.validation.Valid;
import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;

public class BuilderRequestPojo {

	private String dataBase;

	@Valid
	private LinkedList<JoinData> joinDatas;

	@Valid
	private FilterData requestData;

	
	public FilterData getRequestData() {
		return requestData;
	}

	public void setRequestData(FilterData requestData) {
		this.requestData = requestData;
	}

	public LinkedList<JoinData> getJoinDatas() {
		return joinDatas;
	}

	public void setJoinDatas(LinkedList<JoinData> joinDatas) {
		this.joinDatas = joinDatas;
	}

	public String getDataBase() {
		return dataBase;
	}

	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}

}
