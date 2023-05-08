package com.query.builder.dto;

import java.util.List;

public class WhereClause {

	private List<WhereConditionDTO> whereConditon;

	public List<WhereConditionDTO> getWhereConditon() {
		return whereConditon;
	}

	public void setWhereConditon(List<WhereConditionDTO> whereConditon) {
		this.whereConditon = whereConditon;
	}

}
