package org.example.vendingmachine.api.v1.repository;

import org.example.vendingmachine.api.v1.model.ProductModel;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductModel, Integer> {
}
