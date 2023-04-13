//package com.query.builder.common;
//import org.springframework.jdbc.core.RowMapper;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class MyObjectRowMapper implements RowMapper<MyObject> {
//
//    @Override
//    public MyObject mapRow(ResultSet rs, int rowNum) throws SQLException {
//        MyObject myObject = new MyObject();
//        myObject.setColumn1(rs.getString("column1"));
//        myObject.setColumn2(rs.getString("column2"));
//        myObject.setColumn3(rs.getString("column3"));
//        // set other properties of myObject as per the schema and the columns selected in the query
//        return myObject;
//    }
//}