package com.query.builder.request;

import java.util.LinkedList;

import javax.validation.Valid;
import org.springframework.context.annotation.Configuration;

import com.query.builder.dto.FilterData;
import com.query.builder.dto.JoinData;

@Configuration
public class BuilderRequestPojo {

	private String dataBase;

	@Valid
	private LinkedList<JoinData> joinDatas;

	@Valid
	private FilterData filterData;

	public FilterData getFilterData() {
		return filterData;
	}

	public void setFilterData(FilterData filterData) {
		this.filterData = filterData;
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
