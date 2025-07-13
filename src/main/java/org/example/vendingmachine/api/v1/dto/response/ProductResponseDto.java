package org.example.vendingmachine.api.v1.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.vendingmachine.api.v1.model.ProductModel;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ProductResponseDto {
    private int id;
    private String name;
    private int price;
    private int quantity;
    private UserResponseDto seller;

    public ProductResponseDto(ProductModel product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.seller = new UserResponseDto(product.getSeller());
    }

    public ProductResponseDto(ProductModel product, UserResponseDto seller) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "Product:{id: %d, name: %s, price: %d, quantity: %d, seller: %s}"
                .formatted(id, name, price, quantity, seller);
    }
}
