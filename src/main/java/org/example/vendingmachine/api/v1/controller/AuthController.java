package org.example.vendingmachine.api.v1.controller;

import org.example.vendingmachine.api.v1.dto.request.AuthRequestDto;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.dto.response.AuthResponseDto;
import org.example.vendingmachine.api.v1.dto.response.ResponseDto;
import org.example.vendingmachine.api.v1.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<AuthResponseDto>> authUser(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(ResponseDto.ok(authService.authenticateUser(authRequestDto)));
    }
}
