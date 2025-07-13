package org.example.vendingmachine.api.v1.controller;

import org.example.vendingmachine.api.v1.aop.CheckOwnership;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.dto.response.DepositResponseDto;
import org.example.vendingmachine.api.v1.dto.response.ResponseDto;
import org.example.vendingmachine.api.v1.dto.response.UserResponseDto;
import org.example.vendingmachine.api.v1.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<UserResponseDto>> createUser(@RequestBody UserRequestDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.created(userService.createUser(user)));
    }

    @GetMapping("/{id}")
    @CheckOwnership
    public ResponseEntity<ResponseDto<UserResponseDto>> getUser(@PathVariable int id) {
        return ResponseEntity.ok(ResponseDto.ok(userService.getUser(id)));
    }

    @PatchMapping("/{id}")
    @CheckOwnership
    public ResponseEntity<ResponseDto<UserResponseDto>> updateUser(@PathVariable int id, @RequestBody UserRequestDto user) {
        return ResponseEntity.ok(ResponseDto.ok(userService.updateUser(id, user)));
    }

    @DeleteMapping("/{id}")
    @CheckOwnership
    public ResponseEntity<ResponseDto<String>> deleteUser(@PathVariable int id ) {
        return ResponseEntity.ok(ResponseDto.ok(userService.deleteUser(id)));
    }

    @PostMapping("{id}/deposit/{amount}")
    @CheckOwnership
    public ResponseEntity<ResponseDto<DepositResponseDto>> deposit(@PathVariable int id, @PathVariable int amount) {
        return ResponseEntity.ok(ResponseDto.ok(userService.addDeposit(id, amount)));
    }

    @PostMapping("/{id}/reset-deposit")
    @CheckOwnership
    public ResponseEntity<ResponseDto<String>> resetDeposit(@PathVariable int id) {
        return ResponseEntity.ok(ResponseDto.ok(userService.resetDeposit(id)));
    }


}
