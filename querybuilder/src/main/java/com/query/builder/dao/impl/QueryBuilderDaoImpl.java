package com.query.builder.dao.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.dto.WhereConditionDTO;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.request.FilterDataPojo;
import com.query.builder.request.JoinData;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public QueryBuilderDaoImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// 1.get All table Name in Database
	@Override
	public List<Map<String, Object>> getTableNames(String schemaName) {
		String querys = "SELECT table_name FROM information_schema.tables WHERE table_schema ='" + schemaName + "'";
		return jdbcTemplate.queryForList(querys);

	}

	// 2.Get the list list of table name and response it back as table wise select
	// Query
	@Override
	public List<Map<String, Object>> listOfSelectQuery(List<String> tableName, String schemaName) {
		List<Map<String, Object>> results = new ArrayList<>();
		for (String tableNames : tableName) {
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("SELECT * FROM " + schemaName + "." + tableNames);
			List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(sqlQuery.toString());
			Map<String, Object> tableResult = new HashMap<>();
			tableResult.put("table", tableNames);
			tableResult.put("rows", queryResult);
			results.add(tableResult);
		}

		return results;
	}

	// 3 This method will return the column And TableName of the database
	@Override
	public Map<String, Map<String, String>> getColumnAndTableName(String dataBase) {
		Map<String, Map<String, String>> schemaMap = new LinkedHashMap<>();
		List<String> tableNames = new ArrayList<>();

		// Query to get all table names in the database
		String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, dataBase);

		// Iterate over the result set to get table names
		while (rowSet.next()) {
			tableNames.add(rowSet.getString("table_name"));
		}
		// Query to get column names and data types for each table
		sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";

		// Iterate over the table names and execute the query to get column names and
		// data types
		for (String tableName : tableNames) {
			Map<String, String> columnMap = new LinkedHashMap<>();
			rowSet = jdbcTemplate.queryForRowSet(sql, dataBase, tableName);

			// Iterate over the result set to get column names and data types
			while (rowSet.next()) {
				columnMap.put(rowSet.getString("column_name"), rowSet.getString("data_type"));
			}

			// Add the table name and column names and data types to the schema map
			schemaMap.put(tableName, columnMap);
		}
		// Add the table names to the schema map as a separate entry for easy access
		String tableNamesString = String.join(",", tableNames);
		Map<String, String> tableNameMap = new LinkedHashMap<>();
        
		tableNameMap.put("tableNames", tableNamesString);
		schemaMap.put("tableName", tableNameMap);

		return schemaMap;
	}

	// 4. select * from L1 inner join R1 on ((LT1 == RT1) and (Lt2 != RT2))
	// This Api for dynamic join query for multiple tables
	public Object getJoinedData(BuilderRequestPojo builderRequestPojo)
			throws JsonMappingException, JsonProcessingException {
		StringBuilder queryBuilder = new StringBuilder();
		List<JoinData> joinDatas = builderRequestPojo.getJoinData();
		List<String> columnNames = joinDatas.get(0).getColumnNames();
		boolean value = false;
		queryBuilder.append("SELECT ");
		for (String column : columnNames) {
			queryBuilder.append(column).append(", ");
		}
		queryBuilder.setLength(queryBuilder.length() - 2);
		for (int index = 0; index < joinDatas.size(); index++) {
			String leftTableName = joinDatas.get(index).getLsTableName();
			String rightTableName = joinDatas.get(index).getRsTableName();
			String joinType = joinDatas.get(index).getJoinType();
			int joinConditionSize = joinDatas.get(index).getJoinCondition().size();
			String operators = joinDatas.get(index).getOperators();

			for (int condition = 0; condition < joinConditionSize; condition++) {
				String leftJoinColumn = joinDatas.get(index).getJoinCondition().get(condition).getLscolumn();
				String rightJoinColumn = joinDatas.get(index).getJoinCondition().get(condition).getRscolumn();
				String conditionType = joinDatas.get(index).getJoinCondition().get(condition).getConditionType();

				if (index == 0 && condition == 0) {
					queryBuilder.append(" FROM ").append(leftTableName);
					queryBuilder.append(" " + joinType + " ").append(rightTableName).append(" ON ")
							.append(leftTableName).append(".").append(leftJoinColumn).append(" " + conditionType + " ")
							.append(rightTableName).append(".").append(rightJoinColumn);
				} else if (index > 0) {
					queryBuilder.append(" " + joinType + " ").append(leftTableName).append(" ON ").append(leftTableName)
							.append(".").append(leftJoinColumn).append(" " + conditionType + " ").append(rightTableName)
							.append(".").append(rightJoinColumn);
				} else if (condition > 0) {
					queryBuilder.append(" " + operators + " ").append(leftTableName).append(".").append(leftJoinColumn)
							.append(" " + conditionType + " ").append(rightTableName).append(".")
							.append(rightJoinColumn);
				}
			}
		}

		queryBuilder.append(" WHERE ");

		int jsonDataSize = builderRequestPojo.getJoinData().size();
		int whereClauses=builderRequestPojo.getJoinData().get(0).getWhereClause().size();
		int whereConditions=builderRequestPojo.getJoinData().get(jsonDataSize).getWhereClause().get(whereClauses).getWhereConditon().size();
		for (int where = 0; where < whereClauses; where++) {
			List<WhereConditionDTO > whereCondition = builderRequestPojo.getJoinData().get(where).getWhereClause()
					.get(where).getWhereConditon();
			for (int condition = 0; condition < whereCondition.size(); condition++) {
//where ((product_id= 1 and store_details.store_id=1)or(product_name='productBV'));
				String values=whereCondition.get(condition).getValue();
				String operator=whereCondition.get(condition).getOperator();
				String columnName=whereCondition.get(condition).getColumnName();
				
//				if (condition > 0) {
//					queryBuilder.append(" " + builderRequestPojo.getJoinData().get(where).getWhereClause()
//							.get(where).getWhereConditon().get(where).getOperator() + " ");
//				}
				//if (condition==0) {
					queryBuilder.append(" "+columnName +" "+ operator +values);
				//}
				
			}
		}
		
		return jdbcTemplate.queryForList(queryBuilder.toString());

		
	}

	// 5. This Api is filter condition of the selected columns for the tables
	@Override
	public List<Map<String, Object>> intFilterCondition(BuilderRequestPojo builderRequestPojo) {
		List<FilterDataPojo> filterPojos = builderRequestPojo.getFilterPojos();
		List<Map<String, Object>> response = new ArrayList<>();// add the execuited query one by one
		List<String> responseQuery = new ArrayList<>();// returnQuery
		for (int index = 0; index < filterPojos.size(); index++) {
			String column = filterPojos.get(index).getColumnName();
			String table = filterPojos.get(index).getTableNames();
			String whereCondition = filterPojos.get(index).getWhereCondition();
			String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereCondition;
			responseQuery.add(sql);
			List<Map<String, Object>> queryResponse = jdbcTemplate.queryForList(sql);
			Map<String, Object> tableResult = new HashMap<>();
			tableResult.put("tableName", table);
			tableResult.put("whereCondtion", whereCondition);
			tableResult.put("filterResponse", queryResponse);
			response.add(tableResult);
		}
//		System.out.println(responseQuery);
		return response;
	}

	// 6.This API is used to join the table with using inner join without on
	// condition and where conditon
	public List<Map<String, Object>> innerJoin(List<String> tableName) {

		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append("SELECT * FROM " + tableName.get(0));
		for (int index = 1; index < tableName.size(); index++) {
			sqlQuery.append(" INNER JOIN ");
			sqlQuery.append(tableName.get(index));
		}
		System.out.println(sqlQuery.toString());

		return jdbcTemplate.queryForList(sqlQuery.toString());
	}

	// 7.Get Column, value and datatype by using List table name and Column name
	@Override
	public List<Map<String, Object>> getColumnValueDatatype(List<String> tableName, String schemaName) {

		List<Map<String, Object>> rows = new ArrayList<>();

		for (String table : tableName) {
			String sql = "SELECT * FROM " + schemaName + "." + table;
			List<Map<String, Object>> tableRows = jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>() {
				@Override
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSetMetaData metadata = rs.getMetaData();
					Map<String, Object> row = new HashMap<>();
					for (int i = 1; i <= metadata.getColumnCount(); i++) {
						String columnName = metadata.getColumnName(i);
						Object value = rs.getObject(i);
						String dataType = metadata.getColumnTypeName(i);
						Map<String, Object> column = new HashMap<>();
						column.put("value", value);
						column.put("datatype", dataType);
						row.put(columnName, column);
					}
					row.put("tableName", table);
					return row;
				}
			});
			rows.addAll(tableRows);
		}

		return rows;
	}

	// 8.This api for to create group by and using list of column
	@Override
	public List<Map<String, Object>> groupBy(List<String> columnNames, String schemaName, String tableName) {
		// Construct the SELECT and GROUP BY clauses dynamically based on the list of
		// column names
		String selectClause = String.join(",", columnNames);
		String groupByClause = String.join(",", columnNames);

		// Construct the SQL query string with the schema name, SELECT, and GROUP By
		// clauses

		String sql1 = "SELECT" + " " + selectClause + " " + "FROM " + schemaName + "." + tableName + " GROUP BY "
				+ groupByClause;
		// Execute the query using jdbcTemplate
		return jdbcTemplate.queryForList(sql1);

	}

	// Get the column and data(values) using table name and schemaName(db Name)
	@Override
	public List<Map<String, Object>> getColumnValues(String schemaName, String tableName) {
		String sql = "select * from " + schemaName + "." + tableName;
		return jdbcTemplate.queryForList(sql);

	}

	// get the table column by datatype (using the datatype and tableName)
	public List<Map<String, Object>> getTableDataByType(String tableName, String dataType) {
		// SQL query that selects the columns of the specified data type from the
		// specified table
		String query = "SELECT column_name FROM information_schema.columns WHERE table_name = ? AND data_type = ?";
		// Execute the query with the table name and data type as parameters
		return jdbcTemplate.queryForList(query, tableName, dataType);

	}

	// not in use

	// get All column name in particular table (to add multiple tables)
	@Override
	public List<String> getColumnName(String schemaName, String tableName) {
		String query = "SELECT column_name FROM information_schema.columns "
				+ "WHERE table_schema = ? AND table_name = ?";
		return jdbcTemplate.queryForList(query, String.class, schemaName, tableName);

	}

	@Override // not in use
	public List<Map<String, Object>> executeDynamicQuery(List<String> tables, List<String> joins,
			String filterCondition) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ");
		sql.append(String.join(", ", tables));
		sql.append(" ");
		sql.append(String.join(" ", joins));
		sql.append(" WHERE ");
		sql.append(filterCondition);
		return jdbcTemplate.queryForList(sql.toString());
	}

	// Get All Schemas using schemata (get db names)
	@Override
	public List<String> getAllSchemas() {
		String sql = "SELECT schema_name FROM information_schema.schemata";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	// THis method is use for cartesian product
	public List<Map<String, Object>> getColumnListOfTableName(String projectName, List<String> tableNames) {
		List<Map<String, Object>> response = new ArrayList<>();

		for (String tableName : tableNames) {
			String query = "SELECT column_name FROM information_schema.columns WHERE table_name = " + tableName
					+ " AND table_schema = " + projectName;
			// System.out.println(query);
			List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
			Map<String, Object> tableResult = new HashMap<>();
			tableResult.put("tables", result);
			response.add(tableResult);
		}
		return response;
	}

	// get Column and values of by using list of table Name
	// It has problem to connect with database table //cartesianProduct join
	public List<Map<String, Object>> getColumnValueListOfTable(List<String> tableNames, String schemaName) {
		StringJoiner joiner = new StringJoiner(",");
		for (String tableName : tableNames) {
			joiner.add(tableName);
		}
		String tables = joiner.toString();
		String query = "SELECT * from " + schemaName + "." + tables;
		System.out.println(query);// Cartesian product
		return jdbcTemplate.queryForList(query);
	}

	// get the Current database Name from project
	@Override
	public List<String> getDatabaseName() {
		return jdbcTemplate.queryForList("SELECT DATABASE()", String.class);
	}

	public List<String> getColumnNames(String projectName, List<String> tableNames) {
		List<String> columnNames = new ArrayList<>();

		String query = "SELECT column_name FROM information_schema.columns WHERE table_name = ? AND table_schema = ?";

		for (String tableName : tableNames) {
			List<String> result = jdbcTemplate.queryForList(query, new Object[] { tableName, projectName },
					String.class);
			for (String columnName : result) {
				columnNames.add(columnName);
			}
		}

		return columnNames;
	}

	// Get the specific table column of the table with datatype
	@Override
	public Map<String, String> getColumnAndDatatypes(String tableName) {
		Map<String, String> response = new HashMap<>();
		String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = ?";
		jdbcTemplate.query(sql, new Object[] { tableName }, rs -> {
			response.put(rs.getString("column_name"), rs.getString("data_type"));
		});
		return response;
	}
}
