package com.rohanclan.ashpool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;

public class JdbcTest 
{ 
	public void testSource(String url)
	{ 
		try
		{ 
			Driver driver = (Driver)Class.forName("com.rohanclan.ashpool.jdbc.Driver").newInstance(); 
			DriverManager.registerDriver(driver); 
			Connection conn = DriverManager.getConnection(url,null); 
			Statement stmt = conn.createStatement(); 
			java.sql.ResultSet rs; 
			rs = stmt.executeQuery("select tables;");
			
			while(rs.next())
			{ 
				System.out.println(rs.getString(3)); 
			}
		}
		catch(Exception e)
		{ 
			System.err.println("testSource: " + e.toString()); 
			e.printStackTrace(System.err); 
		} 
	} 
	
	public static void main(String args[])
	{ 
		JdbcTest boot = new JdbcTest();
		
		if(args.length < 1)
		{ 
			System.out.println("Usage: JdbcTest <constring>"); 
			System.exit(0); 
		} 
		boot.testSource(args[0]); 
	} 
} 
