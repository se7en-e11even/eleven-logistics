package com.eleven.logistics.gateway.filter;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Primary
    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        return new SwaggerUiConfigParameters(swaggerUiConfigProperties);
    }
    
    @Bean
    public RouterFunction<ServerResponse> routerFunction(SwaggerUiConfigParameters swaggerUiConfigParameters) {
        return RouterFunctions.route()
                .GET("/v3/api-docs/swagger-config", req -> 
                    ServerResponse.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(swaggerUiConfigParameters))
                )
                .build();
    }
    
    @Bean
    public List<SwaggerUiConfigProperties.SwaggerUrl> swaggerUrls() {
        List<SwaggerUiConfigProperties.SwaggerUrl> urls = new ArrayList<>();
        urls.add(createUrl("hub", "/hub-service/v3/api-docs"));
        urls.add(createUrl("slack", "/slack-service/v3/api-docs"));
        return urls;
    }
    
    private SwaggerUiConfigProperties.SwaggerUrl createUrl(String name, String url) {
        SwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new SwaggerUiConfigProperties.SwaggerUrl();
        swaggerUrl.setName(name);
        swaggerUrl.setUrl(url);
        return swaggerUrl;
    }
}