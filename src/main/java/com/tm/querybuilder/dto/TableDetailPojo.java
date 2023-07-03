package com.tm.querybuilder.dto;

import java.util.List;
import java.util.Map;

public class TableDetailPojo {

	Map<String, String> column;

	private String primarykey;
	
	List<ForeignKeys> foreignKeys;

	public Map<String, String> getColumn() {
		return column;
	}

	public String getPrimarykey() {
		return primarykey;
	}

	public List<ForeignKeys> getForeignKeys() {
		return foreignKeys;
	}

	public void setColumn(Map<String, String> column) {
		this.column = column;
	}

	public void setPrimarykey(String primarykey) {
		this.primarykey = primarykey;
	}

	public void setForeignKeys(List<ForeignKeys> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}
	
	
	
	
	
}
