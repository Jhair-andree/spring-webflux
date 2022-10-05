package com.magadiflo.webflux.api.rest.app;

import com.magadiflo.webflux.api.rest.app.models.documents.Categoria;
import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import com.magadiflo.webflux.api.rest.app.models.services.IProductoService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootWebfluxApiRestApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private IProductoService productoService;

    @Test
    public void listarTest() {
        this.client.get()
                .uri("/api/v2/productos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Producto.class)
                .consumeWith(response -> {
                    List<Producto> productos = response.getResponseBody();
                    System.out.println("Total de productos: " + productos.size());
                    productos.forEach(p -> {
                        System.out.println(p.getNombre());
                    });
                    Assertions.assertThat(productos.size() > 0).isTrue();
                });
        //.hasSize(12);
    }

    @Test
    public void verTest() throws InterruptedException {
        //Esperamos unos 3segundos mientras en la clase principal del proyecto se termina de insertar los registros al a BD
        TimeUnit.SECONDS.sleep(3);

        //Es importante usar el block() para obtener un objeto a partir del flujo ya que dentro del pruebas unitarias no se puede usar el subscribe.
        //Para pruebas unitarias tiene que ser SÍNCRONO y NO asíncrono
        Producto producto = this.productoService.findByNombre("Interruptor simple").block();
        System.out.println("Producto encontrado.... " + producto);

        this.client.get()
                .uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto productos = response.getResponseBody();
                    Assertions.assertThat(productos.getId()).isNotEmpty();
                    Assertions.assertThat(productos.getId().length()).isGreaterThan(0);
                    Assertions.assertThat(productos.getNombre()).isEqualTo("Interruptor simple");
                });
        //.jsonPath("$.id").isNotEmpty()
        //.jsonPath("$.nombre").isEqualTo("Interruptor simple");
    }

    @Test
    public void crearTest() {
        Categoria categoria = this.productoService.findCategoriaByNombre("Muebles").block();
        Producto producto = new Producto("Mesa comedor", 100D, categoria);

        this.client.post()
                .uri("/api/v2/productos")
                .contentType(MediaType.APPLICATION_JSON) //Es el media type del request con el cual vamos a enviar el json para crear el producto
                .accept(MediaType.APPLICATION_JSON)//Es la response, el tipo de contenido que queremos manejar en la respuesta, lo que esperamos
                .body(Mono.just(producto), Producto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Mesa comedor")
                .jsonPath("$.categoria.nombre").isEqualTo("Muebles");

    }

    @Test
    public void crear_2_Test() {
        Categoria categoria = this.productoService.findCategoriaByNombre("Muebles").block();
        Producto producto = new Producto("Mesa comedor", 100D, categoria);

        this.client.post()
                .uri("/api/v2/productos")
                .contentType(MediaType.APPLICATION_JSON) //Es el media type del request con el cual vamos a enviar el json para crear el producto
                .accept(MediaType.APPLICATION_JSON)//Es la response, el tipo de contenido que queremos manejar en la respuesta, lo que esperamos
                .body(Mono.just(producto), Producto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto p = response.getResponseBody();
                    Assertions.assertThat(p.getId()).isNotEmpty();
                    Assertions.assertThat(p.getNombre()).isEqualTo("Mesa comedor");
                    Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("Muebles");
                });
    }

    @Test
    public void editarTest() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);

        Producto producto = this.productoService.findByNombre("Celular Huawey").block();//su categoría actual es Electrónico
        Categoria categoria = this.productoService.findCategoriaByNombre("Informática").block();

        Producto productoEditado = new Producto("Huawey Y9 2022", 1500D, categoria);

        this.client.put()
                .uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productoEditado), Producto.class)
                .exchange() // Con exchange, enviamos la request
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Huawey Y9 2022")
                .jsonPath("$.categoria.nombre").isEqualTo("Informática");
    }

}
