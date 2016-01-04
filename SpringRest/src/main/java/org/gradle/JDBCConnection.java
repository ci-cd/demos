package org.gradle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCConnection {
	   // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
	   static final String DB_URL = "jdbc:oracle:thin:@172.73.0.208:10161/xe";

	   //  Database credentials
	   static final String USER = "system";
	   static final String PASS = "oracle";

	   
	   public static List<Employee> getEmployeeList () {
	   Connection conn = null;
	   Statement stmt = null;
	   List<Employee> empList = null;
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName(JDBC_DRIVER);

	      //STEP 3: Open a connection
	      System.out.println("Connecting to database...");
	      conn = DriverManager.getConnection(DB_URL,USER,PASS);

	      //STEP 4: Execute a query
	      System.out.println("Creating statement...");
	      stmt = conn.createStatement();
	      String sql;
	      sql = "SELECT EMP_ID, EMP_NAME, EMP_DEPT_ID, EMP_LOC, EMP_SAL FROM EMPLOYEE";
	      ResultSet rs = stmt.executeQuery(sql);

	      empList = new ArrayList<Employee>();
	      //STEP 5: Extract data from result set
	      while(rs.next()){
	    	  
	    	  Employee emp = new Employee();
	         //Retrieve by column name
	         emp.setEmpId(rs.getInt("EMP_ID")+"");
	         emp.setEmpName(rs.getString("EMP_NAME"));
	         emp.setDeptId(rs.getInt("EMP_DEPT_ID")+"");
	         emp.setLocation(rs.getString("EMP_LOC"));
	         emp.setSal(rs.getInt("EMP_SAL")+"");
	         
	        empList.add(emp);
	      }
	      //STEP 6: Clean-up environment
	      rs.close();
	      stmt.close();
	      conn.close();
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }finally{
	      //finally block used to close resources
	      try{
	         if(stmt!=null)
	            stmt.close();
	      }catch(SQLException se2){
	      }// nothing we can do
	      try{
	         if(conn!=null)
	            conn.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	   }//end try

	   return empList;
	}//end main

}
