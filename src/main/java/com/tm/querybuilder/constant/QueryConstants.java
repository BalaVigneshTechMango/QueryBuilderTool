package com.tm.querybuilder.constant;

public class QueryConstants {

	private QueryConstants() {
	}

	public static final String IS_SCHEMA_EXIST = "SELECT count(table_schema) FROM information_schema.tables WHERE table_schema = :schemaName ";
	public static final String FETCH_TABLE_DETAILS="SELECT table_name FROM information_schema.tables WHERE table_schema = :schemaName";

	public static final String GET_DATATYPE="SELECT concat(table_name,'.',column_name) as table_column,data_type FROM information_schema.columns WHERE table_schema = :schemaName and table_name in (:tableName) and concat(table_name,'.',column_name) in (:columns)";
    
	public static final String IS_VALID_TABLE="SELECT count(*) FROM information_schema.tables WHERE table_name IN (:tableName) AND table_schema = :schemaName";
	public static final String IS_VALID_COLUMN="SELECT count(*) FROM information_schema.columns WHERE table_schema = :schemaName AND table_name IN (:tableName) AND concat(table_name,'.',column_name) IN (:columns)";
	
	public static final String FETCH_COLUMN_DETAILS ="SELECT column_name, data_type,table_name FROM information_schema.columns WHERE table_schema = :schemaName AND table_name IN (:tableName)";
	
	public static final String SCHEMA_DETAILS="SELECT column_details.data_type,column_details.column_name,table_details.table_name FROM information_schema.columns as column_details INNER JOIN information_schema.tables as table_details ON column_details.table_schema=table_details.table_schema AND column_details.table_name=table_details.table_name WHERE column_details.table_schema = :schemaName";

	public static final String FROM=" FROM ";
	public static final String SELECT="SELECT ";
	
}
