package com.magadiflo.webflux.api.rest.app.controllers;

import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> listar() {
        return Mono.just(ResponseEntity.ok() //Status: 200
                .contentType(MediaType.APPLICATION_JSON)//Por defecto es del tipo application json
                .body(this.productoService.findAll())//Guarda el contenido en el body
        );
    }

    @GetMapping(path = "/{id}")
    public Mono<ResponseEntity<Producto>> ver(@PathVariable String id) {
        return this.productoService.findById(id)
                .map(producto -> ResponseEntity.ok().body(producto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Producto>> crear(@RequestBody Producto producto) {
        if (producto.getCreateAt() == null) {
            producto.setCreateAt(new Date());
        }
        return this.productoService.save(producto).map(prod -> ResponseEntity
                .created(URI.create("/api/productos/".concat(prod.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(prod));
    }

    @PutMapping(path = "/{id}")
    public Mono<ResponseEntity<Producto>> editar(@RequestBody Producto producto, @PathVariable String id) {
        return this.productoService.findById(id)
                .flatMap(prod -> {
                    prod.setNombre(producto.getNombre());
                    prod.setCategoria(producto.getCategoria());
                    prod.setPrecio(producto.getPrecio());
                    return this.productoService.save(prod);
                })
                .map(p -> ResponseEntity
                        .created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
