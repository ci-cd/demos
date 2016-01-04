package org.gradle;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("System Environment:");
        for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
            out.println(envvar.getKey() + ": " + envvar.getValue());
        }
        
        out.println("Data Source : " + config.applicationInfo().toString());
        
        
        
        List<ServiceInfo> serviceInfos = config.serviceInfo();
        for (ServiceInfo serviceInfo : serviceInfos) {
        	out.println( " ServiceInfo of type : " + serviceInfo.getClass().getName());
            if (serviceInfo instanceof RelationalServiceInfo) {
                out.println(" JDBC URL : " + ((RelationalServiceInfo) serviceInfo).getJdbcUrl());
            }
            
            
        }
       
        out.println(" Oracle DataSource : " + config.oracleDataSource());

    }
}
