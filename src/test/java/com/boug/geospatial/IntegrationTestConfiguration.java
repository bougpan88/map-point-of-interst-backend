package com.boug.geospatial;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static com.boug.geospatial.PointOfInterestIntegrationTest.postgreSQLContainer;

@SpringBootConfiguration
public class IntegrationTestConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
