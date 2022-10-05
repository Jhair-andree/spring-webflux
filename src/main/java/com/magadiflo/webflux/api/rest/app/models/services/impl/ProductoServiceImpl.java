package com.magadiflo.webflux.api.rest.app.models.services.impl;

import com.magadiflo.webflux.api.rest.app.models.documents.Categoria;
import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.repository.CategoriaRepository;
import com.magadiflo.webflux.api.rest.app.models.repository.ProductoRepository;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements IProductoService {

    public final ProductoRepository productoRepository;
    public final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Flux<Producto> findAll() {
        return this.productoRepository.findAll();
    }

    @Override
    public Flux<Producto> findAllConNombreUppercase() {
        return this.productoRepository.findAll()
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                });
    }

    @Override
    public Flux<Producto> findAllConNombreUppercaseRepeat() {
        return this.findAllConNombreUppercase().repeat(5000);// repetimos 5000 veces el flujo actual
    }

    @Override
    public Mono<Producto> findById(String id) {
        return this.productoRepository.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return this.productoRepository.save(producto);
    }

    @Override
    public Mono<Void> delete(Producto producto) {
        return this.productoRepository.delete(producto);
    }

    @Override
    public Mono<Producto> findByNombre(String nombre) {
        return this.productoRepository.obtenerPorNombre(nombre);
    }

    @Override
    public Flux<Categoria> findAllCategoria() {
        return this.categoriaRepository.findAll();
    }

    @Override
    public Mono<Categoria> findCategoriaById(String id) {
        return this.categoriaRepository.findById(id);
    }

    @Override
    public Mono<Categoria> saveCategoria(Categoria categoria) {
        return this.categoriaRepository.save(categoria);
    }

    @Override
    public Mono<Categoria> findCategoriaByNombre(String nombre) {
        return this.categoriaRepository.findByNombre(nombre);
    }

}
