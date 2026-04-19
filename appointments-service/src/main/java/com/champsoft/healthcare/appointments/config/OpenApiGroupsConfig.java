package com.champsoft.healthcare.appointments.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

    @Bean
    GroupedOpenApi appointmentsApi() {
        return GroupedOpenApi.builder()
                .group("agents").
                pathsToMatch("/api/agents/**")
                .build();
    }

}
