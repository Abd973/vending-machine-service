package org.example.vendingmachine.api.v1.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.vendingmachine.api.v1.exception.AccessDeniedException;
import org.example.vendingmachine.api.v1.exception.NotFoundException;
import org.example.vendingmachine.api.v1.model.ProductModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class ProductOwnershipAspect extends AbstractOwnershipAspect {

    private final UserRepository userRepository;

    @Override
    @Before("@annotation(checkProductOwnership) && within(org.example.vendingmachine.api.v1.controller.ProductController)")
    public void checkOwnership(JoinPoint joinPoint, CheckOwnership checkProductOwnership) {

        Integer productId = getIdParam(joinPoint, checkProductOwnership);

        int authUserId = getAuthenticatedId();

       UserModel authUser = userRepository.findById(authUserId).orElseThrow(() -> new NotFoundException("User", authUserId));
       List<ProductModel> authUserProduct = authUser.getProducts().stream().filter(p -> p.getId() == productId).toList();

        if (authUserProduct.size() != 1) {
            throw new AccessDeniedException("Product");
        }

    }
}
