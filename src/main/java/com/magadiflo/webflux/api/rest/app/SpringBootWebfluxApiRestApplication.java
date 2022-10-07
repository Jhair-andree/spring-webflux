package com.magadiflo.webflux.api.rest.app;

import com.magadiflo.webflux.api.rest.app.models.documents.Categoria;
import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluxApiRestApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootWebfluxApiRestApplication.class);
    private final IProductoService productoService;

    private final ReactiveMongoTemplate mongoTemplate;

    public SpringBootWebfluxApiRestApplication(IProductoService productoService, ReactiveMongoTemplate mongoTemplate) {
        this.productoService = productoService;
        this.mongoTemplate = mongoTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApiRestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        this.mongoTemplate.dropCollection("productos").subscribe();
        this.mongoTemplate.dropCollection("categorias").subscribe();

        Categoria electronico = new Categoria("Electrónico");
        Categoria deporte = new Categoria("Deporte");
        Categoria informatica = new Categoria("Informática");
        Categoria muebles = new Categoria("Muebles");
        Categoria decoracion = new Categoria("Decoración");

        Flux.just(electronico, deporte, informatica, muebles, decoracion)
                .flatMap(this.productoService::saveCategoria)
                .doOnNext(categoria -> LOG.info("Categoría insertada: id={}, nombre={}", categoria.getId(), categoria.getNombre()))
                .thenMany(
                        Flux.just(
                                        new Producto("Tv LG 70", 3609.40, electronico),
                                        new Producto("Sony Cámara HD Digital", 680.60, electronico),
                                        new Producto("Bicicleta Monteñera", 1800.60, deporte),
                                        new Producto("Monitor 27' LG", 750.00, electronico),
                                        new Producto("Teclado Micronics", 17.00, informatica),
                                        new Producto("Celular Huawey", 900.00, electronico),
                                        new Producto("Interruptor simple", 6.00, decoracion),
                                        new Producto("Pintura Satinado", 78.00, decoracion),
                                        new Producto("Pintura Base", 10.00, decoracion),
                                        new Producto("Sillón 3 piezas", 10.00, muebles),
                                        new Producto("Separador para TV", 10.00, muebles),
                                        new Producto("Juego de mesa 8 sillas", 10.00, muebles)
                                )
                                .flatMap(producto -> {
                                    producto.setCreateAt(new Date());
                                    return this.productoService.save(producto);
                                })
                ).subscribe(
                        producto -> LOG.info("Insert: {} {}", producto.getId(), producto.getNombre()),
                        error -> LOG.error(error.getMessage()),
                        () -> LOG.info("save success!")
                );
    }
}
