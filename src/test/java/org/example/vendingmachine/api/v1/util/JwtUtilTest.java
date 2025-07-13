package org.example.vendingmachine.api.v1.util;

import org.example.vendingmachine.TestHelper;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(JwtUtil.class)
@ContextConfiguration(classes = JwtUtil.class)
@TestPropertySource(
        properties = {
                "jwt.secret-key=AS1234DFHGEGEWRFTASDG#SecretGeneratedHashKey",
                "jwt.expiration-ms=3600000"
        })
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateToken_ShouldReturnExpectedResponse_WhenInputIsValid() {
        UserRequestDto userRequestDto = TestHelper.getSellerUserRequestDto();
        UserModel userModel = TestHelper.getUserModel(userRequestDto);
        String actualToken = jwtUtil.generateToken(userModel);

        assertFalse(actualToken.isEmpty());
        assertTrue(jwtUtil.isValidToken(actualToken));
        assertEquals(userModel.getId(), jwtUtil.getUserId(actualToken));
        assertEquals(userModel.getRole().toString(), jwtUtil.getRole(actualToken));
    }
}
