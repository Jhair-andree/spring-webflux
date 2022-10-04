package com.magadiflo.webflux.api.rest.app.config;

import com.magadiflo.webflux.api.rest.app.handler.ProductoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductoHandler handler) { //Desacoplamos el handler de esta clase de configuración y lo llevamos a una clase distinta (ProductoHandler)
        return RouterFunctions
                .route(RequestPredicates.GET("/api/v2/productos").or(RequestPredicates.GET("/api/v3/productos")),handler::listar)
                .andRoute(RequestPredicates.GET("/api/v2/productos/{id}"), handler::ver)
                .andRoute(RequestPredicates.POST("/api/v2/productos"), handler::crear)
                .andRoute(RequestPredicates.PUT("/api/v2/productos/{id}"), handler::editar)
                .andRoute(RequestPredicates.DELETE("/api/v2/productos/{id}"), handler::eliminar)
                .andRoute(RequestPredicates.POST("/api/v2/productos/upload/{id}"), handler::upload)
                .andRoute(RequestPredicates.POST("/api/v2/productos/producto-con-foto"), handler::crearConFoto);
    }

}
