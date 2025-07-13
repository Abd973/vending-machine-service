package org.example.vendingmachine.api.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DepositResponseDto {
    private UserResponseDto user;
    private int depositAdded;
}
