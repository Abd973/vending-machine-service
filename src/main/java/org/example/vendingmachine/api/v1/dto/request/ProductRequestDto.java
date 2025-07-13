package org.example.vendingmachine.api.v1.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    @NotBlank(message = "Price cannot be blank")
    private int price;
    @NotBlank(message = "Quantity cannot be blank")
    private int quantity;
}
