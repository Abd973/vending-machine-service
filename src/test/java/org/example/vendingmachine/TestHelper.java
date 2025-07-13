package org.example.vendingmachine;

import org.example.vendingmachine.api.v1.dto.Role;
import org.example.vendingmachine.api.v1.dto.request.ProductBuyRequestDto;
import org.example.vendingmachine.api.v1.dto.request.ProductRequestDto;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.model.ProductModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestHelper {

    private static int userIdCounter = 1;
    private static int productIdCounter = 1;

    public static UserRequestDto getBuyerUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("Dummy_BUYER_Name");
        userRequestDto.setPassword("Dummy_Password");
        userRequestDto.setRole(Role.BUYER);
        userRequestDto.setDeposit(10);
        return userRequestDto ;
    }

    public static UserRequestDto getSellerUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("Dummy_SELLER_Name");
        userRequestDto.setPassword("Dummy_Password");
        userRequestDto.setRole(Role.SELLER);
        return userRequestDto ;
    }

    public static UserModel getUserModel(UserRequestDto userRequestDto) {
        return new UserModel(userRequestDto);
    }

    public static ProductModel getProductModel(ProductRequestDto productRequestDto, UserModel userModel) {
        ProductModel productModel = new ProductModel(productRequestDto);
        productModel.setSeller(userModel);
        return productModel;
    }

    public static ProductRequestDto getProductRequestDto(int sellerId) {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setName("Dummy_PRODUCT_Name");
        productRequestDto.setPrice(5);
        productRequestDto.setQuantity(10);
        return productRequestDto;
    }

}
