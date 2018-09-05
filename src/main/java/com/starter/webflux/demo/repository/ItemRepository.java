package com.starter.webflux.demo.repository;


import com.starter.webflux.demo.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

}
