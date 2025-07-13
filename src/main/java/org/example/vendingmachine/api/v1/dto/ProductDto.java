package org.example.vendingmachine.api.v1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private int id;
    private String name;
    private double cost;
    private int amount;
    private int sellerId;

}
