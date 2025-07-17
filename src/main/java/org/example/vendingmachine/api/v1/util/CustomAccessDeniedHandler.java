package org.example.vendingmachine.api.v1.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        String responseBody = """
                {
                        "data": null,
                         "errorMessage": "Access denied, You ROLE does not match the required one to access this endpoint",
                         "status": "FORBIDDEN"
                }
                """;
        response.getWriter().write(responseBody);
    }
}
