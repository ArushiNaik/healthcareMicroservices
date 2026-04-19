package com.champsoft.healthcare.billings.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

    @Bean
    GroupedOpenApi billingsApi() {
        return GroupedOpenApi.builder()
                .group("billings").
                pathsToMatch("/api/billings/**")
                .build();
    }

}
