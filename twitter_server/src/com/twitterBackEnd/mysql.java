package com.twitterBackEnd;

import java.sql.*;
public class mysql {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/twitter_db";
	static final String USER = "root";
	static final String PASS = "patel";
	
    public ResultSet fetchData(String query) throws SQLException, ClassNotFoundException
    {
    		Connection con = null;
    	    Statement stmt = null;
    	    //System.out.println("Connecting to database13123.");
    	    Class.forName("com.mysql.jdbc.Driver");
    	    System.out.println("Connecting to database...");
    		con = DriverManager.getConnection(DB_URL,USER,PASS);
    		
    		stmt = con.createStatement();
    		System.out.println("******************");
    		ResultSet rs = stmt.executeQuery(query);
    		
    		return rs;
    }
    
    public int insertData(String query) throws SQLException, ClassNotFoundException
    {
		Connection con = null;
	    Statement stmt = null;
	    //System.out.println("Connecting to database13123.");
	    Class.forName("com.mysql.jdbc.Driver");
	    System.out.println("Connecting to database...");
		con = DriverManager.getConnection(DB_URL,USER,PASS);
		
		stmt = con.createStatement();
		System.out.println("******************");
		int i= stmt.executeUpdate(query);
		System.out.println("11");
		
		return i;
    }


}
