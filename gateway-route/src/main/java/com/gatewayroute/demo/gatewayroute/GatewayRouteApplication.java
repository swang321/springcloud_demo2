package com.gatewayroute.demo.gatewayroute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayRouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayRouteApplication.class, args);
    }


    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("gateway-route1", r -> r.path("/about").uri("http://ityouknow.com"))
                .build();
    }

}
