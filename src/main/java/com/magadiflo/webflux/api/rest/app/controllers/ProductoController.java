package com.magadiflo.webflux.api.rest.app.controllers;

import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoService productoService;

    @Value("${config.uploads.path}")
    private String uploadPath;

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

    @DeleteMapping(path = "/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id) {
        return this.productoService.findById(id)
                .flatMap(p -> this.productoService.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/upload/{id}")
    public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart FilePart file) {
        return this.productoService.findById(id)
                .flatMap(producto -> {
                    producto.setFoto(UUID.randomUUID().toString()
                            .concat("-")
                            .concat(file.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", "")
                            )
                    );
                    return file.transferTo(new File(this.uploadPath.concat(producto.getFoto())))
                            .then(this.productoService.save(producto));
                }).map(producto -> ResponseEntity.ok().body(producto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
