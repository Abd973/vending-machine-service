package org.example.vendingmachine.api.v1.controller;

import org.example.vendingmachine.api.v1.aop.CheckOwnership;
import org.example.vendingmachine.api.v1.dto.request.ProductBuyRequestDto;
import org.example.vendingmachine.api.v1.dto.request.ProductRequestDto;
import org.example.vendingmachine.api.v1.dto.response.ProductBuyResponseDto;
import org.example.vendingmachine.api.v1.dto.response.ProductResponseDto;
import org.example.vendingmachine.api.v1.dto.response.ResponseDto;
import org.example.vendingmachine.api.v1.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<ProductResponseDto>> createProduct(@RequestBody ProductRequestDto productRequestDto, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.created(productService.createProduct(productRequestDto, authHeader)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ProductResponseDto>> getProduct(@PathVariable int id) {
        return ResponseEntity.ok(ResponseDto.ok(productService.getProduct(id)));
    }

    @PatchMapping("/{id}")
    @CheckOwnership
    public ResponseEntity<ResponseDto<ProductResponseDto>> updateProduct(@PathVariable int id, @RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.ok(ResponseDto.ok(productService.updateProduct(id, productRequestDto)));
    }

    @DeleteMapping("/{id}")
    @CheckOwnership
    public ResponseEntity<ResponseDto<String>> deleteProduct(@PathVariable int id) {
        return ResponseEntity.ok(ResponseDto.ok(productService.deleteProduct(id)));
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<ResponseDto<ProductBuyResponseDto>> buyProduct(@PathVariable int id, @RequestBody ProductBuyRequestDto productBuyRequestDto, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(ResponseDto.ok(productService.buyProduct(id, productBuyRequestDto, authHeader)));
    }
}
