package com.magadiflo.webflux.api.rest.app.handler;

import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Esta clase hará el papel del controlador en el fondo,
 * o de Handler, pero de forma Reactiva
 */

@Component
public class ProductoHandler {

    private final IProductoService productoService;

    public ProductoHandler(IProductoService productoService) {
        this.productoService = productoService;
    }

    public Mono<ServerResponse> listar(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.productoService.findAll(), Producto.class);
    }

}
