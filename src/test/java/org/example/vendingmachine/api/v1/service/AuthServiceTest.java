package org.example.vendingmachine.api.v1.service;


import org.example.vendingmachine.TestHelper;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.model.TokenModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.TokenRepository;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.example.vendingmachine.api.v1.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    protected UserModel getMockFindUserById() {
        UserRequestDto buyerUserDto = TestHelper.getBuyerUserRequestDto();
        int userId = 1;
        UserModel mockedUser = new UserModel(buyerUserDto);
        mockedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        return mockedUser;
    }


//    @Test
//    void generateNewToken_shouldReturnToken() {
//        UserModel mockedUser = getMockFindUserById();
//
//        String expectedToken = "ValidToken";
//        when(jwtUtil.generateToken(mockedUser)).thenReturn(expectedToken);
//        Date now = new Date();
//        Date expireTime = new Date(now + authService.EXPIRATION_OFFSET);
//        TokenModel expectedTokenModel = new TokenModel(expectedToken, now, )
//        when(tokenRepository.save(any(TokenModel.class))).thenReturn()
//
//        TokenModel response = authService.generateNewToken(mockedUser, new Date());
//
//        assertTrue(response.isValid());
//    }
}
