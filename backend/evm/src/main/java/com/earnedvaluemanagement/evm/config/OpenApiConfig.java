package com.earnedvaluemanagement.evm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI evmOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("EVM - Earned Value Management API")
                        .description("REST API for managing projects and activities with automated "
                                + "Earned Value Management (EVM) indicator calculations. Supports CRUD "
                                + "operations and calculates PV, EV, CV, SV, CPI, SPI, EAC, and VAC.")
                        .version("1.0.0")
                        .contact(new Contact().name("EVM Development Team")));
    }
}
