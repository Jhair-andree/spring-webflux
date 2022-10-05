package com.magadiflo.webflux.api.rest.app.models.repository;

import com.magadiflo.webflux.api.rest.app.models.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {
    Mono<Categoria> findByNombre(String nombre);

}
