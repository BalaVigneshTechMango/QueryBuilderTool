package com.query.builder.dao.impl;

import java.util.ArrayList;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.aspectj.weaver.ast.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.TSFBuilder;
import com.query.builder.dao.QueryBuilderDao;
import com.query.builder.request.BuilderRequestPojo;
import com.query.builder.request.FilterPojo;
import com.query.builder.request.JoinConditionDto;
import com.query.builder.request.JoinData;
import com.query.builder.response.QueryResponsePojo;

import ch.qos.logback.classic.db.names.ColumnName;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public QueryBuilderDaoImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// get All table Name in Database
	@Override
	public List<Map<String, Object>> getTableNames(String schemaName) {
		String querys = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
		return jdbcTemplate.queryForList(querys,schemaName);
	
	}

	// get All column name in particular table
	@Override
	public List<String> getColumnName(String schemaName, String tableName) {
		String query = "SELECT column_name FROM information_schema.columns "
				+ "WHERE table_schema = ? AND table_name = ?";
		return jdbcTemplate.queryForList(query, String.class, schemaName, tableName);

	}

	@Override
	public List<Map<String, Object>> groupBy(List<String> columnNames, String schemaName, String tableName) {
		// Construct the SELECT and GROUP BY clauses dynamically based on the list of
		// column names
		String selectClause = String.join(",", columnNames);
		String groupByClause = String.join(",", columnNames);

		// Construct the SQL query string with the schema name, SELECT, and GROUP By
		// clauses
//		String sql = String.format("SELECT %s FROM %s." + tableName + " GROUP BY %s", selectClause, schemaName,
//				groupByClause)
		String sql1 = "SELECT" + " " + selectClause + " " + "FROM " + schemaName + "." + tableName + " GROUP BY "
				+ groupByClause;
		// Execute the query using jdbcTemplate
		return jdbcTemplate.queryForList(sql1);

	}

	// This method will return the column And TableName of the database CH
	@Override
	public List<Map<String, Object>> getColumnAndTableName(String schemaName) {
		return jdbcTemplate.query("SELECT table_name, column_name FROM information_schema.columns WHERE table_schema ="
				+ "'" + schemaName + "'", new ColumnMapRowMapper());
	}

	// Get All Schemas using schemata (get db names)
	@Override
	public List<String> getAllSchemas() {
		String sql = "SELECT schema_name FROM information_schema.schemata";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	// Get the specific table column of the table with datatype
	@SuppressWarnings("deprecation")
	@Override
	public Map<String, String> getColumnAndDatatypes(String tableName) {
		Map<String, String> response = new HashMap<>();
		String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = ?";
		jdbcTemplate.query(sql, new Object[] { tableName }, rs -> {
			response.put(rs.getString("column_name"), rs.getString("data_type"));
		});
		return response;
	}

	// Get the column and data(values) using table name and schemaName(db Name)
	@Override
	public List<Map<String, Object>> getColumnValues(String schemaName, String tableName) {
		String sql = "select * from " + schemaName + "." + tableName;
		return jdbcTemplate.queryForList(sql);

	}

	// get Column and values of by using list of table Name
	// It has problem to connect with database table
	@Override
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

	// get the table column by datatype (using the datatype and tableName)
	public List<Map<String, Object>> getTableDataByType(String tableName, String dataType) {
		// SQL query that selects the columns of the specified data type from the
		// specified table
		String query = "SELECT column_name FROM information_schema.columns WHERE table_name = ? AND data_type = ?";
		// Execute the query with the table name and data type as parameters
		return jdbcTemplate.queryForList(query, tableName, dataType);

	}

	// get the Current database Name from project
	@Override
	public List<String> getDatabaseName() {
		return jdbcTemplate.queryForList("SELECT DATABASE()", String.class);
	}

	// This Api is filter condition of the selected columns for the tables
	@Override
	public List<Map<String, Object>> intFilterCondition(BuilderRequestPojo builderRequestPojo) {
		List<FilterPojo> filterPojos = builderRequestPojo.getFilterPojos();
		List<Map<String, Object>> response = new ArrayList<>();
		for (int index = 0; index < filterPojos.size(); index++) {
			String column = filterPojos.get(index).getColumnName();
			String table = filterPojos.get(index).getTableNames();
			String whereCondition = filterPojos.get(index).getWhereCondition();
			String sql = "SELECT " + column + " FROM " + table + " WHERE " + whereCondition;
			List<Map<String, Object>> queryResponse = jdbcTemplate.queryForList(sql);
			response.addAll(queryResponse);
		}
		return response;
	}

	// select * from L1 inner join R1 on ((LT1 == RT1) and (Lt2 != RT2))
	// This Api for dynamic join query for multiple tables
	public List<Map<String, Object>> getJoinedData(BuilderRequestPojo builderRequestPojo) {
		StringBuilder queryBuilder = new StringBuilder();
		List<JoinData> joinDatas = builderRequestPojo.getJoinData();
		List<String> columnNames = joinDatas.get(0).getColumnNames();
		String whereCondition = joinDatas.get(0).getWhereCondition();
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
		queryBuilder.append(" WHERE ").append(whereCondition);
		System.out.println(queryBuilder.toString());
		return jdbcTemplate.queryForList(queryBuilder.toString());
	}

	@Override
	public List<Map<String, Object>> getColumnValueDatatype(String tableName, String schemaName) {
		String sql = "SELECT * FROM " + tableName;
		return jdbcTemplate.query(sql, rs -> {	
			ResultSetMetaData metadata = rs.getMetaData();
			List<Map<String, Object>> rows = new ArrayList<>();
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					String columnName = metadata.getColumnName(i);
					Object value = rs.getObject(i);
					String dataType = metadata.getColumnTypeName(i);
					row.put(columnName, value + " (" + dataType + ")");
				}
				rows.add(row);
			}
			return rows;
		});
	}
	
	
	@Override//not in use
	public List<Map<String, Object>> executeDynamicQuery(List<String> tables,List<String> joins, String filterCondition) {
	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT * FROM ");
	    sql.append(String.join(", ", tables));
	    sql.append(" ");
	    sql.append(String.join(" ", joins));
	    sql.append(" WHERE ");
	    sql.append(filterCondition);
	    return jdbcTemplate.queryForList(sql.toString());
	}


}
