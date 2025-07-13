package org.example.vendingmachine.api.v1.service;

import org.example.vendingmachine.api.v1.dto.request.ProductBuyRequestDto;
import org.example.vendingmachine.api.v1.dto.request.ProductRequestDto;
import org.example.vendingmachine.api.v1.dto.response.ProductBuyResponseDto;
import org.example.vendingmachine.api.v1.dto.response.SoldProductInfoDto;
import org.example.vendingmachine.api.v1.dto.response.ProductResponseDto;
import org.example.vendingmachine.api.v1.exception.InsufficientBalanceException;
import org.example.vendingmachine.api.v1.exception.InsufficientProductQuantityException;
import org.example.vendingmachine.api.v1.exception.NotFoundException;
import org.example.vendingmachine.api.v1.model.ProductModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.ProductRepository;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.example.vendingmachine.api.v1.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ProductResponseDto getProduct(int id) {
        return productRepository
                .findById(id)
                .map(ProductResponseDto::new)
                .orElseThrow(() -> new NotFoundException("Product", id));
    }

    public ProductResponseDto createProduct(ProductRequestDto product, String authHeader) {
        int userId = jwtUtil.getUserId(authHeader.substring(7));
        UserModel sellerUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));

        ProductModel newProduct = new ProductModel(product);
        newProduct.setSeller(sellerUser);
        ProductModel savedProduct = productRepository.save(newProduct);

        return new ProductResponseDto(savedProduct);
    }

    public ProductResponseDto updateProduct(int id, ProductRequestDto product) {
        ProductModel existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());

        ProductModel updatedProduct = productRepository.save(existingProduct);

        return new ProductResponseDto(updatedProduct);
    }
    @Transactional
    public String deleteProduct(int id) {
        ProductModel product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));

        UserModel seller = product.getSeller();
        seller.getProducts().remove(product);
        product.setSeller(null);
        productRepository.deleteById(id);
        return "Product with ID %s has been deleted successfully".formatted(id);
    }


    @Transactional
    public ProductBuyResponseDto buyProduct(int id, ProductBuyRequestDto productBuyRequestDto, String authHeader) {
        int userId = jwtUtil.getUserId(authHeader.substring(7));
        UserModel buyerUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));

        ProductModel existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));

        int productPrice = existingProduct.getPrice();
        int productQuantity = existingProduct.getQuantity();
        int requestedQuantity = productBuyRequestDto.getRequestedQuantity();
        int buyerAvailableBalance = buyerUser.getDeposit();

        checkBuyProductConstraints(productPrice, productQuantity, requestedQuantity, buyerAvailableBalance);

        return handleBuyProduct(existingProduct, productBuyRequestDto, buyerUser);
    }

    protected List<Integer> getRemainingChanges(UserModel user) {
        List<Integer> changes = new ArrayList<>();
        int availableBalance = user.getDeposit();
        List<Integer> allowedChanges = Stream
                .of(5, 10, 20, 50, 100)
                .sorted(Comparator.reverseOrder())
                .toList();

        for (Integer change : allowedChanges) {
            if (availableBalance == 0)
                break;
            while (availableBalance - change >= 0) {
                availableBalance -= change;
                changes.add(change);
            }
        }

        return changes;
    }

    protected void checkBuyProductConstraints(int productPrice, int productQuantity, int requestedQuantity, int buyerAvailableBalance) {
        int totalCost = requestedQuantity * productPrice;

        if (requestedQuantity < 1)
            throw new RuntimeException("Requested quantity must be greater than zero, your requested quantity = " + requestedQuantity);

        if (requestedQuantity > productQuantity)
            throw new InsufficientProductQuantityException(productQuantity, requestedQuantity);

        if (buyerAvailableBalance < totalCost)
            throw new InsufficientBalanceException(buyerAvailableBalance);
    }

    protected ProductBuyResponseDto handleBuyProduct(ProductModel existingProduct, ProductBuyRequestDto productBuyRequestDto, UserModel user) {

        int totalCost = productBuyRequestDto.getRequestedQuantity() * existingProduct.getPrice();

        existingProduct.setQuantity(existingProduct.getQuantity() - productBuyRequestDto.getRequestedQuantity());
        ProductModel updatedProduct = productRepository.save(existingProduct);

        user.setDeposit(user.getDeposit() - totalCost);
        UserModel updatedUser = userRepository.save(user);

        List<Integer> changes = getRemainingChanges(updatedUser);

        return new ProductBuyResponseDto(totalCost, new SoldProductInfoDto(updatedProduct.getName(), productBuyRequestDto.getRequestedQuantity(), updatedProduct.getPrice()), changes);
    }
}
