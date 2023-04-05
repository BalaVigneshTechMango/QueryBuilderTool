package com.query.builder.common;

public class MyObject {

	private String column1;
	private String column2;
	private String column3;
	// add other properties as needed
	
	// default constructor
	public MyObject() {
	}
	
	// constructor with parameters
	public MyObject(String column1, String column2, String column3) {
	    this.column1 = column1;
	    this.column2 = column2;
	    this.column3 = column3;
	    // set other properties as needed
	}
	
	// getter and setter methods for column1
	public String getColumn1() {
	    return column1;
	}
	
	public void setColumn1(String column1) {
	    this.column1 = column1;
	}
	
	// getter and setter methods for column2
	public String getColumn2() {
	    return column2;
	}
	
	public void setColumn2(String column2) {
	    this.column2 = column2;
	}
	
	// getter and setter methods for column3
	public String getColumn3() {
	    return column3;
	}
	
	public void setColumn3(String column3) {
	    this.column3 = column3;
	}
	
	// add other getter and setter methods for other properties as needed
	

}
