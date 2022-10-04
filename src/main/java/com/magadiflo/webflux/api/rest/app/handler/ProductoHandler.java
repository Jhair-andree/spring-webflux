package com.magadiflo.webflux.api.rest.app.handler;

import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

/**
 * Esta clase hará el papel del controlador en el fondo,
 * o de Handler, pero de forma Reactiva
 */

@Component
public class ProductoHandler {

    private final IProductoService productoService;

    @Value("${config.uploads.path}")
    private String uploadFile;

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

    public Mono<ServerResponse> crear(ServerRequest request) {
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);
        return productoMono.flatMap(producto -> {
            if (producto.getCreateAt() == null) {
                producto.setCreateAt(new Date());
            }
            return this.productoService.save(producto);
        }).flatMap(p -> ServerResponse
                .created(URI.create("/api/v2/productos/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p)
        );
    }

    public Mono<ServerResponse> editar(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);
        Mono<Producto> productoMonoDB = this.productoService.findById(id);
        //zipWith, recordar que lo usamos para combinar
        return productoMonoDB.zipWith(productoMono, (prodBD, prodReq) -> {
            prodBD.setNombre(prodReq.getNombre());
            prodBD.setPrecio(prodReq.getPrecio());
            prodBD.setCategoria(prodReq.getCategoria());
            return prodBD;
        }).flatMap(producto -> ServerResponse
                .created(URI.create("/api/v2/productos/".concat(producto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.productoService.save(producto), Producto.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> productoMonoDB = this.productoService.findById(id);
        return productoMonoDB.flatMap(producto -> this.productoService.delete(producto).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> upload(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.multipartData()
                .map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> this.productoService.findById(id).flatMap(producto -> {
                    producto.setFoto(UUID.randomUUID().toString()
                            .concat("-")
                            .concat(filePart.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", ""))
                    );
                    return filePart.transferTo(new File(this.uploadFile + producto.getFoto())).then(this.productoService.save(producto));
                }))
                .flatMap(p -> ServerResponse
                        .created(URI.create("/api/v2/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
