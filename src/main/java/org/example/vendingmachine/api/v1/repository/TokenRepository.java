package org.example.vendingmachine.api.v1.repository;

import org.example.vendingmachine.api.v1.model.TokenModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TokenRepository extends CrudRepository<TokenModel, Long> {
    List<TokenModel> findAllByUserId(int userId);
}
