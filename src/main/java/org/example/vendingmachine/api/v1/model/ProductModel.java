package org.example.vendingmachine.api.v1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vendingmachine.api.v1.dto.request.ProductRequestDto;

@Entity
@Table(name = "products")
@Setter
@Getter
@NoArgsConstructor
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name= "price", nullable = false)
    private int price;

    @Column(name= "quantity", nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private UserModel seller;

    public ProductModel(ProductRequestDto productDto) {
        this.name = productDto.getName();
        this.price = productDto.getPrice();
        this.quantity = productDto.getQuantity();
    }
}
