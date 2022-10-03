package com.magadiflo.webflux.api.rest.app.handler;

import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
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
                .body(this.productoService.findAll(), Producto.class); //Aquí estamos pasando un flujo de tipo reactivo y el objeto con el que corresponde
    }

    public Mono<ServerResponse> ver(ServerRequest request) {
        String id = request.pathVariable("id");
        return this.productoService.findById(id)
                .flatMap(producto -> ServerResponse.ok().bodyValue(producto)) //Aquí como se está emitiendo un objeto normal, es decir un producto (no es un tipo reactivo, es decir no es un Flux ni un Mono) usamos bodyValue(...)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
