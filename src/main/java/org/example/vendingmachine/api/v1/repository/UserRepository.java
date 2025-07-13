package org.example.vendingmachine.api.v1.repository;

import org.example.vendingmachine.api.v1.model.UserModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserModel, Integer> {

    Optional<UserModel> findByName(String name);
}
