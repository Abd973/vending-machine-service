package org.example.vendingmachine.api.v1.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.vendingmachine.api.v1.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserOwnershipAspect extends AbstractOwnershipAspect {

    @Before("@annotation(checkUserOwnership) && within(org.example.vendingmachine.api.v1.controller.UserController)")
    public void checkOwnership(JoinPoint joinPoint, CheckOwnership checkUserOwnership) {

        Integer pathId = getIdParam(joinPoint, checkUserOwnership);

        int userId = getAuthenticatedId();

        if (userId != pathId) {
            throw new AccessDeniedException("User");
        }
    }
}
