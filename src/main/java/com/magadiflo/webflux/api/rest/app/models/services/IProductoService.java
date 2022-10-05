package com.magadiflo.webflux.api.rest.app.models.services;

import com.magadiflo.webflux.api.rest.app.models.documents.Categoria;
import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductoService {

    //Producto
    Flux<Producto> findAll();

    Flux<Producto> findAllConNombreUppercase();

    Flux<Producto> findAllConNombreUppercaseRepeat();

    Mono<Producto> findById(String id);

    Mono<Producto> save(Producto producto);

    Mono<Void> delete(Producto producto);

    Mono<Producto> findByNombre(String nombre);

    //Categoría
    Flux<Categoria> findAllCategoria();

    Mono<Categoria> findCategoriaById(String id);

    Mono<Categoria> saveCategoria(Categoria categoria);

    Mono<Categoria> findCategoriaByNombre(String nombre);

}
