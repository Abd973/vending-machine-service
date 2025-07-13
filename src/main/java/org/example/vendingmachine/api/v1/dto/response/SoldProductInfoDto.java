package org.example.vendingmachine.api.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SoldProductInfoDto {
    private String productName;
    private int soldQuantity;
    private int unitPrice;
}
