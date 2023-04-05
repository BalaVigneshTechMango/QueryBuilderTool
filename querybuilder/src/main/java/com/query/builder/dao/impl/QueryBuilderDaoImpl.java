package com.query.builder.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.query.builder.common.MyObject;
import com.query.builder.common.MyObjectRowMapper;
import com.query.builder.dao.QueryBuilderDao;

@Service
public class QueryBuilderDaoImpl implements QueryBuilderDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// get All table Name in Database
	@Override
	public List<String> getTableNames() {
		String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
		return jdbcTemplate.query(query, new Object[] { "mallproject" }, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet resultSet, int i) throws SQLException {
				return resultSet.getString("table_name");
			}
		});

	}

	// get All column name in particular table
	@Override
	public List<String> getColumnName() {
		String query = "SELECT column_name FROM information_schema.columns "
				+ "WHERE table_schema = ? AND table_name = ?";
		return jdbcTemplate.queryForList(query, String.class, "mallproject", "mall_details");

	}

	@Override
	public List<MyObject> groupBy(List<String> columnNames) {
		// Construct the SELECT and GROUP BY clauses dynamically based on the list of
		// column names
		String selectClause = String.join(",", columnNames);
		String groupByClause = String.join(",", columnNames);

		// Construct the SQL query string with the schema name, SELECT, and GROUP BY
		// clauses
		String sql = String.format("SELECT %s FROM %s.EmployeeList GROUP BY %s", selectClause, "meetingRoom",
				groupByClause);

		// Execute the query using jdbcTemplate
		// List<MyObject> results =
		return jdbcTemplate.query(sql, new MyObjectRowMapper() {
		});

	}

	// This method will return the column And TableName of the database
	@Override
	public Object getColumnAndTableName() {
		// List<Map<String, Object>> results =
		return jdbcTemplate.query("SELECT table_name, column_name " + "FROM information_schema.columns "
				+ "WHERE table_schema = 'mallproject'", new ColumnMapRowMapper());
	}

	// Get All Schemas using schemata (get db names)
	@Override
	public List<String> getAllSchemas() {
		String sql = "SELECT schema_name FROM information_schema.schemata";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	// Get the specific table column of the table with datatype
	@Override
	public Map<String, String> getColumnAndDatatypes() {
		Map<String, String> response = new HashMap<>();
		String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = ?";
		jdbcTemplate.query(sql, new Object[] { "mall_details" }, rs -> {
			response.put(rs.getString("column_name"), rs.getString("data_type"));
		});
		return response;
	}

	// Get the column and data(values) using table name and schemaName(db Name)
	@Override
	public List<Map<String, Object>> getColumnValues() {
		String sql = "select * from mallproject.mall_details";
		List<Map<String, Object>> tableData = jdbcTemplate.queryForList(sql);
		return tableData;
	}

	// get Column and values of by using list of table Name
	// It has problem to connect with database table
	@Override
	public List<Map<String, Object>> getColumnValueListOfTable(List<String> tableNames) {
		StringJoiner joiner = new StringJoiner(",");
		for (String tableName : tableNames) {
			joiner.add(tableName);
		}
		String tables = joiner.toString();

		String query = "SELECT * from mallproject" + "." + tables;

		List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

		return result;
	}

	// joiner = new StringJoiner(",");
//    for (String schemaName : schemaNames) {
//        joiner.add(schemaName);
//    }
//    String schemas = joiner.toString();
}
