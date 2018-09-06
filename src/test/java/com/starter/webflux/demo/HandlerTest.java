package com.starter.webflux.demo;


import com.starter.webflux.demo.model.Item;
import com.starter.webflux.demo.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class HandlerTest {

    private WebTestClient client;

    private List<Item> expectedItems;

    @Autowired
    private ItemRepository repository;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        this.client = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/items").build();

        this.expectedItems = repository.findAll().collectList().block();
    }

    @Test
    void testGetAllProducts() {
        client
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Item.class)
                .isEqualTo(expectedItems);
    }

    @Test
    void testItemInvalidIdNotFound() {
        client.get().uri("/aaa").exchange().expectStatus().isNotFound();
    }
}
