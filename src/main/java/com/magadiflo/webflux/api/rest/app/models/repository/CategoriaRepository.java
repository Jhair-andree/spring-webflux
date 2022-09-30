package com.magadiflo.webflux.api.rest.app.models.repository;

import com.magadiflo.webflux.api.rest.app.models.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {
}
