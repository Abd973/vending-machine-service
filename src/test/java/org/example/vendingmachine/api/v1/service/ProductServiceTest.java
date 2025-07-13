package org.example.vendingmachine.api.v1.service;

import org.apache.catalina.User;
import org.example.vendingmachine.TestHelper;
import org.example.vendingmachine.api.v1.dto.request.ProductBuyRequestDto;
import org.example.vendingmachine.api.v1.dto.request.ProductRequestDto;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.dto.response.ProductBuyResponseDto;
import org.example.vendingmachine.api.v1.dto.response.ProductResponseDto;
import org.example.vendingmachine.api.v1.dto.response.SoldProductInfoDto;
import org.example.vendingmachine.api.v1.dto.response.UserResponseDto;
import org.example.vendingmachine.api.v1.exception.InsufficientBalanceException;
import org.example.vendingmachine.api.v1.exception.InsufficientProductQuantityException;
import org.example.vendingmachine.api.v1.exception.NotFoundException;
import org.example.vendingmachine.api.v1.model.ProductModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.ProductRepository;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProductService productService;


    protected ProductModel getMockFindProductById(UserModel user) {
        ProductRequestDto productRequestDto = TestHelper.getProductRequestDto(user.getId());
        int productId = 10;
        ProductModel mockedProduct = new ProductModel(productRequestDto);
        mockedProduct.setId(productId);
        mockedProduct.setPrice(productRequestDto.getPrice());
        mockedProduct.setSeller(user);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockedProduct));
        return mockedProduct;
    }

    protected UserModel getMockFindUserById() {
        UserRequestDto buyerUserDto = TestHelper.getBuyerUserRequestDto();
        int userId = 1;
        UserModel mockedUser = new UserModel(buyerUserDto);
        mockedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        return mockedUser;
    }


    @Test
    void getProduct_shouldReturnExpectedResponse_whenInputIsValid() {
        UserModel user = TestHelper.getUserModel(TestHelper.getSellerUserRequestDto());
        ProductModel mockedProduct = getMockFindProductById(user);

        ProductResponseDto responseDto = productService.getProduct(mockedProduct.getId());

        verify(productRepository).findById(mockedProduct.getId());
        assertEquals(new ProductResponseDto(mockedProduct, new UserResponseDto(user)), responseDto);
    }

    @Test
    void getProduct_shouldThrowException_whenProductNotFound() {
        int productId = 10;

        NotFoundException expectedException = new NotFoundException("Product", productId);
        when(productRepository.findById(productId)).thenThrow(expectedException);


        NotFoundException result = assertThrows(NotFoundException.class, () -> productService.getProduct(productId));
        assertEquals(expectedException, result);
    }

    @Test
    void updateProduct_shouldReturnExpectedResponse_whenInputIsValid() {

        UserModel user = TestHelper.getUserModel(TestHelper.getSellerUserRequestDto());

        ProductModel mockedProduct = getMockFindProductById(user);

        ProductRequestDto productRequestDto = TestHelper.getProductRequestDto(user.getId());
        when(productRepository.save(any(ProductModel.class))).thenReturn(mockedProduct);

        ProductResponseDto response = productService.updateProduct(mockedProduct.getId(), productRequestDto);
        verify(productRepository).save(any(ProductModel.class));
    }

    @Test
    void deleteProduct_ShouldReturnExpectedResponse_WhenInputIsValid() {
        UserModel user = TestHelper.getUserModel(TestHelper.getSellerUserRequestDto());
        ProductModel mockedProduct = getMockFindProductById(user);


        doNothing()
                .when(productRepository)
                .deleteById(mockedProduct.getId());

        String actualResponse = productService.deleteProduct(mockedProduct.getId());

        verify(productRepository, times(1)).deleteById(mockedProduct.getId());
        assertTrue(actualResponse.contains(mockedProduct.getId() + " has been deleted successfully"));
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        int productId = 1;
        NotFoundException expectedException = new NotFoundException("Product", productId);
        when(productRepository.findById(productId)).thenThrow(expectedException);


        NotFoundException result = assertThrows(NotFoundException.class, () -> productService.deleteProduct(productId));
        assertEquals(expectedException, result);
    }

    @Test
    void remainingChanges_ShouldReturnExpectedResponse_WhenInputIsValid() {
        UserModel user = TestHelper.getUserModel(TestHelper.getBuyerUserRequestDto());
        user.setDeposit(275);

        List<Integer> expectedChanges = List.of(100, 100, 50, 20, 5);

        List<Integer> actualChanges = productService.getRemainingChanges(user);

        assertArrayEquals(expectedChanges.toArray(), actualChanges.toArray());

    }

    @Test
    void checkProductBuyConstraints_ShouldThrowRuntimeException_WhenRequestedQuantityLessThanZero() {
        int productPrice = 10, productQuantity = 15, requestedQuantity = -3, buyerAvailableBalance = 100;

        String errorMessage = "Requested quantity must be greater than zero, your requested quantity = " + requestedQuantity;
        RuntimeException expectedException = new RuntimeException(errorMessage);

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> productService.checkBuyProductConstraints(productPrice, productQuantity, requestedQuantity, buyerAvailableBalance));

        assertEquals(expectedException.getMessage(), actualException.getMessage());
    }

    @Test
    void checkProductBuyConstraints_ShouldThrowInsufficientProductQuantityException_WhenRequestedQuantityMoreThanAvailable() {
        int productPrice = 10, productQuantity = 15, requestedQuantity = 16, buyerAvailableBalance = 100;

        InsufficientProductQuantityException expectedException = new InsufficientProductQuantityException(productQuantity, requestedQuantity);

        InsufficientProductQuantityException actualException = assertThrows(InsufficientProductQuantityException.class, () -> productService.checkBuyProductConstraints(productPrice, productQuantity, requestedQuantity, buyerAvailableBalance));

        assertEquals(expectedException, actualException);
    }


    @Test
    void checkProductBuyConstraints_ShouldThrowInsufficientBalanceException_WhenBuyerAvailableBalanceNotEnough() {
        int productPrice = 10, productQuantity = 15, requestedQuantity = 1, buyerAvailableBalance = 5;

        InsufficientBalanceException expectedException = new InsufficientBalanceException(buyerAvailableBalance);

        InsufficientBalanceException actualException = assertThrows(InsufficientBalanceException.class, () -> productService.checkBuyProductConstraints(productPrice, productQuantity, requestedQuantity, buyerAvailableBalance));

        assertEquals(expectedException, actualException);
    }

    @Test
    void handleBuyProduct_shouldReturnExpectedResponse_WhenInputIsValid() {
        UserModel user = TestHelper.getUserModel(TestHelper.getBuyerUserRequestDto());
        user.setDeposit(275);
        List<Integer> expectedChanges = List.of(100, 100, 50, 10);

        ProductModel product = TestHelper.getProductModel(TestHelper.getProductRequestDto(100), user);

        int requestedQuantity = 3;
        ProductBuyRequestDto productBuyRequestDto = new ProductBuyRequestDto(product.getId(),  requestedQuantity);

        int totalCost = requestedQuantity * product.getPrice();
        SoldProductInfoDto expectedSoldProductInfo = new SoldProductInfoDto(product.getName(), requestedQuantity, product.getPrice());
        ProductBuyResponseDto expected = new ProductBuyResponseDto(totalCost, expectedSoldProductInfo, expectedChanges);

        when(productRepository.save(any(ProductModel.class))).thenReturn(product);
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        ProductBuyResponseDto actual  = productService.handleBuyProduct(product, productBuyRequestDto, user);

        assertEquals(expected, actual);
    }
}
