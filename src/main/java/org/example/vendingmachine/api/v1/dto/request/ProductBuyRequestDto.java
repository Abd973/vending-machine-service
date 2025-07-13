package org.example.vendingmachine.api.v1.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProductBuyRequestDto {
    private int productId;
    private int requestedQuantity;
}
