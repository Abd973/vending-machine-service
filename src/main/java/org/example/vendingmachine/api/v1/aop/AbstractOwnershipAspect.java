package org.example.vendingmachine.api.v1.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class AbstractOwnershipAspect {

    @Before("@annotation(checkOwnership)")
    public abstract void checkOwnership(JoinPoint joinPoint, CheckOwnership checkOwnership);

    public Integer getIdParam(JoinPoint joinPoint, CheckOwnership ownershipChecker) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        Integer pathId = null;
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(ownershipChecker.idParam())) {
                pathId = (Integer) args[i];
                break;
            }
        }

        if (pathId == null) {
            throw new IllegalArgumentException("Path variable 'id' not found in method.");
        }
        return pathId;
    }

    public int getAuthenticatedId() {
        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();
        return Integer.parseInt(auth.getPrincipal().toString());
    }
}
