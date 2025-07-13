package org.example.vendingmachine.api.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vendingmachine.api.v1.dto.Role;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private UserResponseDto user;
    private String token;
    private Date expireAt;
}
