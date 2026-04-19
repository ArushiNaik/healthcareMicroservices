package com.champsoft.healthcare.patients.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

    @Bean
    GroupedOpenApi patientsApi() {
        return GroupedOpenApi.builder()
                .group("patients").
                pathsToMatch("/api/patients/**")
                .build();
    }

}
