package com.champsoft.healthcare.doctors.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

    @Bean
    GroupedOpenApi doctorsApi() {
        return GroupedOpenApi.builder()
                .group("doctors").
                pathsToMatch("/api/doctors/**")
                .build();
    }

}
