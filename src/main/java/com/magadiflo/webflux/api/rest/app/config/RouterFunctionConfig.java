package com.magadiflo.webflux.api.rest.app.config;

import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    private final IProductoService productoService;

    public RouterFunctionConfig(IProductoService productoService) {
        this.productoService = productoService;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route(
                RequestPredicates.GET("/api/v2/productos").or(RequestPredicates.GET("/api/v3/productos")), //Si queremos mapear el mismo resultado a más de una ruta
                request -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.productoService.findAll(), Producto.class)
        );
    }

}
