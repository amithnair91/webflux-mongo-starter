package com.starter.webflux.demo;

import com.starter.webflux.demo.model.Item;
import com.starter.webflux.demo.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

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
}
