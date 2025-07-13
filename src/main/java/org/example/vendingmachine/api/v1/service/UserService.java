package org.example.vendingmachine.api.v1.service;

import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.dto.response.DepositResponseDto;
import org.example.vendingmachine.api.v1.dto.response.UserResponseDto;
import org.example.vendingmachine.api.v1.exception.NotFoundException;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final Set<Integer> allowedDeposit =  new HashSet<>(List.of(5, 10, 20, 50, 100));
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(UserRequestDto userDto) {
        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encryptedPassword);
        UserModel savedUser = userRepository.save(new UserModel(userDto));
        return new UserResponseDto(savedUser);
    }

    public UserResponseDto getUser(int id) {
        return userRepository
                .findById(id).map(UserResponseDto::new).orElseThrow(() -> new RuntimeException("User with ID %s not found".formatted(id)));
    }

    public UserResponseDto updateUser(int id, UserRequestDto userDto) {
        UserModel currUser = userRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID = %s not found".formatted(userDto)));

        currUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        UserModel updatedUser = userRepository.save(currUser);

        return new UserResponseDto(updatedUser);
    }

    public String deleteUser(int id) {
        if (!userRepository.existsById(id))
            throw new NotFoundException("User", id);
        userRepository
                .deleteById(id);
        return "User with ID %s has been deleted successfully".formatted(id);
    }

    public DepositResponseDto addDeposit(int id, int amount) {
        if(!allowedDeposit.contains(amount))
            throw new RuntimeException("%s is invalid deposit amount, only %s are allowed".formatted(amount, allowedDeposit));

        UserModel user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User %s not found".formatted(id)));

        user.setDeposit(user.getDeposit() + amount);
        userRepository.save(user);

        return new DepositResponseDto(new UserResponseDto(user), amount);
    }

    public String resetDeposit(int id) {

        UserModel user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User %s not found".formatted(id)));

        user.setDeposit(0);
        userRepository.save(user);

        return "User with ID %s has been reset successfully".formatted(id);
    }
}