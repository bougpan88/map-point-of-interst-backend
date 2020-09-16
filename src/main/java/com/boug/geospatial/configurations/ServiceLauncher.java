package com.boug.geospatial.configurations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableJpaRepositories(basePackages = {"com.boug.geospatial"})
@ComponentScan({"com.boug.geospatial"})
@EntityScan({"com.boug.geospatial"})
public class ServiceLauncher {

	public static void main(String[] args) {
		SpringApplication.run(ServiceLauncher.class, args);
	}

}
