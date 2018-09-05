package com.starter.webflux.demo.handler;

import com.starter.webflux.demo.model.Item;
import com.starter.webflux.demo.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemHandler {

    private ItemRepository itemRepository;

    public ItemHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        Flux<Item> items = itemRepository.findAll();

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(items, Item.class);
    }

    public Mono<ServerResponse> getItem(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Item> itemMono = itemRepository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return itemMono.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(item)))
                .switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> saveItem(ServerRequest request) {
        Mono<Item> itemMono = request.bodyToMono(Item.class);

        return itemMono.flatMap(item ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemRepository.save(item), Item.class));
    }


    public Mono<ServerResponse> updateItem(ServerRequest request) {
        Mono<Item> updateMono = request.bodyToMono(Item.class);

        String id = request.pathVariable("id");

        Mono<Item> existingProductMono = itemRepository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return updateMono.zipWith(existingProductMono, (updateItem, existingItem) ->
                new Item(existingItem.getId(), existingItem.getName(), existingItem.getPrice()))
                .flatMap(item -> ServerResponse.ok().body(fromObject(item)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteItem(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        Mono<Item> existingItemMono = itemRepository.findById(id);
        return existingItemMono.flatMap(item ->
                ServerResponse.ok()
                        .build(itemRepository.deleteById(id)))
                .switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> deleteAllItems(ServerRequest request) {
        return ServerResponse.ok().build(itemRepository.deleteAll());
    }

}
