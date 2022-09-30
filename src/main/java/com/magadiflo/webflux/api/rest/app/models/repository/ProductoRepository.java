package com.magadiflo.webflux.api.rest.app.models.repository;


import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoRepository extends ReactiveMongoRepository<Producto, String> {

    

}
