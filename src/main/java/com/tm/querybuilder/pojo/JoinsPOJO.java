package com.tm.querybuilder.pojo;

import java.util.List;

public class JoinsPOJO {

	private Boolean isPrimaryKey;
	
	private List<JoinDataPOJO> join;

	public List<JoinDataPOJO> getJoin() {
		return join;
	}

	public void setJoin(List<JoinDataPOJO> join) {
		this.join = join;
	}

	public Boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}

	public void setIsPrimaryKey(Boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	
	
}