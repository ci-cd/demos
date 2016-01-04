package org.gradle;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;



@org.springframework.context.annotation.Configuration
@Profile("cloud")
public class CloudConfiguration extends AbstractCloudConfig{
     
	@Bean
	public ApplicationInstanceInfo applicationInfo(){
		return cloud().getApplicationInstanceInfo();
	}
	
	@Bean
	public List<ServiceInfo> serviceInfo(){
		return cloud().getServiceInfos();
	}
	
	@Bean
	public DataSource oracleDataSource() {
		return connectionFactory().dataSource("xe");
	}
}
