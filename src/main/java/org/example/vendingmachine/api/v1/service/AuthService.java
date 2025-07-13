package org.example.vendingmachine.api.v1.service;

import org.example.vendingmachine.api.v1.dto.request.AuthRequestDto;
import org.example.vendingmachine.api.v1.dto.response.AuthResponseDto;
import org.example.vendingmachine.api.v1.dto.response.UserResponseDto;
import org.example.vendingmachine.api.v1.model.TokenModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.TokenRepository;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.example.vendingmachine.api.v1.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

//    @Value("jwt.expiration-ms")
//    private long expirationOffset;

protected long EXPIRATION_OFFSET = 86400000L; // 1 day


    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, TokenRepository tokenRepository ,PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto authenticateUser(AuthRequestDto authRequestDto) {

        UserModel user = userRepository
                .findByName(authRequestDto.getUserName())
                .orElseThrow(() -> new RuntimeException("User with name %s not found".formatted(authRequestDto.getUserName())));

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword()))
            throw new RuntimeException("Passwords don't match");


        List<TokenModel> tokens = tokenRepository.findAllByUserId(user.getId());
        Date timeNow = new Date(System.currentTimeMillis());
        TokenModel token = null;

        if (!tokens.isEmpty()) {
            token = tokens.get(tokens.size() - 1);
            if (token.getExpireAt().before(timeNow)){
                token.setValid(false);
                tokenRepository.save(token);
                token = generateNewToken(user, timeNow);
            }
        } else {
            token = generateNewToken(user, timeNow);
        }

        if(token == null){
            throw new RuntimeException("Failed to generate token");
        }

        return new AuthResponseDto(new UserResponseDto(user), token.getToken(),  token.getExpireAt());
    }

    protected TokenModel generateNewToken(UserModel user, Date timeNow) {
        String token = jwtUtil.generateToken(user);
        Date expirationDate  = new Date(System.currentTimeMillis() + EXPIRATION_OFFSET);
        TokenModel tokenModel = new TokenModel(token, timeNow, expirationDate, user, true);
        return tokenRepository.save(tokenModel);
    }
}
