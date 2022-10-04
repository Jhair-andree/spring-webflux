package com.magadiflo.webflux.api.rest.app;

import com.magadiflo.webflux.api.rest.app.models.documents.Producto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootWebfluxApiRestApplicationTests {

    @Autowired
    private WebTestClient client;

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

}
