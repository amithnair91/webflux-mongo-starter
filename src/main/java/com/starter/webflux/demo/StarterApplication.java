package com.starter.webflux.demo;

import com.starter.webflux.demo.handler.ItemHandler;
import com.starter.webflux.demo.model.Item;
import com.starter.webflux.demo.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class StarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarterApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ItemRepository repository) {
        return args -> {
            Item firstItem = new Item(null, "first Item", 50.00);
            Item secondItem = new Item(null, "second item", 100.00);

            Flux<Item> itemFlux = Flux.just(firstItem,
                    secondItem).flatMap(repository::save);

            itemFlux.thenMany(repository.findAll()).subscribe(System.out::println);
        };
    }

    @Bean
    RouterFunction<ServerResponse> routes(ItemHandler handler) {
        return route(GET("/").and(accept(APPLICATION_JSON)), handler::getAllItems)
                .andRoute(POST("/items").and(contentType(APPLICATION_JSON)), handler::saveItem)
                .andRoute(DELETE("/items").and(contentType(APPLICATION_JSON)), handler::deleteAllItems)
                .andRoute(GET("/items/{id}").and(contentType(APPLICATION_JSON)), handler::getItem)
                .andRoute(PUT("/items/{id}").and(contentType(APPLICATION_JSON)), handler::updateItem)
                .andRoute(DELETE("/items/{id}").and(contentType(APPLICATION_JSON)), handler::deleteItem);
    }
}
