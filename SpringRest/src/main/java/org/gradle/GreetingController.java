package org.gradle;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.RelationalServiceInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
        
    @Autowired
    CloudConfiguration config;


    @RequestMapping(method = RequestMethod.GET, value = "greeting/{name}")
    public Greeting greeting(@PathVariable String name){
    	
    	if ( "Koti".equalsIgnoreCase(name)) {
    		System.exit(1);
    	}
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    @RequestMapping("/env")
    public void env(HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><body bgcolor='#E6E6FA'>");
        out.println("<h2  style='font-family:verdana;font-size:80%;'>System Environment : </h2>");
        
        out.println("<table border='1' style='font-family:verdana;font-size:80%;'> <tr><th> Key </th> <th> Value </th> <tr> ");
        for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
        	
        	out.println("<tr><td>" + envvar.getKey() + " </td><td>" + envvar.getValue() + " </td></tr>");
        }
        out.println("</table>");
        
        out.println("<h2  style='font-family:verdana;font-size:80%;'>Application Instance Info : </h2>");
        out.println("<table border='1' style='font-family:verdana;font-size:80%;'> <tr><th> Key </th> <th> Value </th> <tr> ");
        Map<String,Object> propMap = config.applicationInfo().getProperties();
        
        for ( Map.Entry<String, Object> entry : propMap.entrySet() ) {
        	out.println("<tr><td>" +entry.getKey() + " </td><td> " + entry.getValue().toString() +" </td></tr>");
        }
        out.println("</table>");
        
        out.println("<h2  style='font-family:verdana;font-size:80%;'>Services Info : </h2>");
        out.println("<table border='1'  style='font-family:verdana;font-size:80%;'> <tr><th> Parameter </th> <th> Value </th> <tr> ");
        List<ServiceInfo> serviceInfos = config.serviceInfo();
        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceInfo instanceof RelationalServiceInfo) {
            	out.println(" <tr><td> Service ID </td><td>" + ((RelationalServiceInfo) serviceInfo).getId() + "</td></tr>");
                out.println(" <tr><td> JDBC URL </td><td>" + ((RelationalServiceInfo) serviceInfo).getJdbcUrl() + "</td></tr>");
                out.println(" <tr><td> Service Host </td><td>" + ((RelationalServiceInfo) serviceInfo).getHost() + "</td></tr>");
                out.println(" <tr><td> Service Type </td><td>" + ((RelationalServiceInfo) serviceInfo).getClass().getName() + "</td></tr>");
                
            }
        }
        out.println("<tr><td> Oracle DataSource</td><td>" + config.oracleDataSource() + " </td><tr>");
        out.println("<tr><td> Oracle Connection</td><td>" + config.oracleDataSource().getConnection() + " </td><tr>");
        out.println("</table></body></html>");

    }
    
    @RequestMapping("/getDBData")
    public void getDBData(HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body bgcolor='#E6E6FA'>");
        out.println("<h1 style='font-family:verdana;font-size:80%;'> Table Data </h1>");
        
        Connection conn = null;
        Statement stmt = null;
        try{
           conn = config.oracleDataSource().getConnection();
           stmt = conn.createStatement();

           String sql = "SELECT EMP_ID,EMP_NAME,EMP_DEPT_ID,EMP_LOC,EMP_SAL FROM Employee";
          
           ResultSet rs = stmt.executeQuery(sql);

           out.println("<table border='1' style='font-family:verdana;'><tr><th>EMP_ID</th><th>EMP_NAME</th><th>DEPT_ID</th><th>EMP_LOC</th><th>EMP-SAL<th></tr>");
           //STEP 5: Extract data from result set
           while(rs.next()){
              //Retrieve by column name
              int id  = rs.getInt("EMP_ID");
              String name = rs.getString("EMP_NAME");
              int deptId = rs.getInt("EMP_DEPT_ID");
              String location = rs.getString("EMP_LOC");
              int sal  = rs.getInt("EMP_SAL");
             
              out.println("<tr><td>" + id + "</td>" + "<td>" + name + "</td>" + "<td>" + deptId + "</td>" + "<td>" + location + "</td>" + "<td>" + sal + "</td></tr>");
           }
           
           out.println("</table></body></html>");
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
        
    }
}
