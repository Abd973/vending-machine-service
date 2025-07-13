package org.example.vendingmachine.api.v1.service;

import org.example.vendingmachine.TestHelper;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.dto.response.DepositResponseDto;
import org.example.vendingmachine.api.v1.dto.response.UserResponseDto;
import org.example.vendingmachine.api.v1.exception.NotFoundException;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;


    protected UserModel getMockFindUserById() {
        UserRequestDto buyerUserDto = TestHelper.getBuyerUserRequestDto();
        int userId = 1;
        UserModel mockedUser = new UserModel(buyerUserDto);
        mockedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        return mockedUser;
    }

    @Test
    public void createUser_ShouldReturnExpectedResponse_WhenInputIsValid() {
        UserRequestDto buyerUserDto = TestHelper.getBuyerUserRequestDto();
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-pass");
        UserModel savedUser = new UserModel(buyerUserDto);
        when(userRepository.save(any(UserModel.class))).thenReturn(savedUser);


        UserResponseDto result = userService.createUser(buyerUserDto);

        verify(userRepository).save(any(UserModel.class));
        assertEquals(new UserResponseDto(savedUser), result);
    }

    @Test
    public void getUser_ShouldReturnExpectedResponse_WhenInputIsValid() {
        UserModel mockedUser = getMockFindUserById();

        UserResponseDto result = userService.getUser(mockedUser.getId());
        verify(userRepository).findById(mockedUser.getId());

        assertEquals(new UserResponseDto(mockedUser), result);
    }

    @Test
    public void getUser_ShouldThrowException_WhenUserNotFound() {
        int userId = 1;

        NotFoundException expectedException = new NotFoundException("User", userId);
        when(userRepository.findById(userId)).thenThrow(expectedException);

        NotFoundException result = assertThrows(NotFoundException.class, () -> userService.getUser(userId));
        assertEquals(expectedException, result);
    }


    @Test
    public void updateUser_ShouldReturnExpectedResponse_WhenInputIsValid() {
        UserModel mockedUser = getMockFindUserById();
        String encryptedPassword = "Encrypted-Password";
        mockedUser.setPassword(encryptedPassword);

        UserRequestDto buyerUserRequestDto = TestHelper.getBuyerUserRequestDto();
        buyerUserRequestDto.setPassword("anyPassword");

        when(passwordEncoder.encode(buyerUserRequestDto.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserModel.class))).thenReturn(mockedUser);

        UserResponseDto expectedResponse = new UserResponseDto(mockedUser);

        UserResponseDto actualResponse = userService.updateUser(mockedUser.getId(), buyerUserRequestDto);

        verify(userRepository).save(any(UserModel.class));
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void deleteUser_ShouldReturnExpectedResponse_WhenInputIsValid() {
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing()
                .when(userRepository)
                .deleteById(userId);

        String actualResponse = userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
        assertTrue(actualResponse.contains(userId + " has been deleted successfully"));
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(false);
        NotFoundException expectedException = new NotFoundException("User", userId);

        NotFoundException result = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        assertEquals(expectedException, result);
    }

    @Test
    void addDeposit_ShouldThrowException_WhenInputIsInvalidAmount() {
        int userId = 1;
        int invalidDepositAmount = 3; // not one of the allowed amounts

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> userService.addDeposit(userId, invalidDepositAmount));

       assertTrue(actualException.getMessage().contains(invalidDepositAmount + " is invalid deposit amount"));
    }

    @Test
    void addDeposit_ShouldReturnExpectedResponse_WhenInputIsValid() {
        int depositAmount = 5; // one of the allowed amounts
        UserModel mockedUser = getMockFindUserById();
        int oldDepositAmount = 100;
        mockedUser.setDeposit(oldDepositAmount);

        DepositResponseDto responseDto = userService.addDeposit(mockedUser.getId(), depositAmount);

        assertEquals(depositAmount, responseDto.getDepositAdded());
        assertEquals(oldDepositAmount + depositAmount, responseDto.getUser().getDeposit());
    }

    @Test
    void resetDeposit_shouldReturnExpectedResponse_WhenInputIsValid() {
        UserModel mockedUser = getMockFindUserById();
        mockedUser.setDeposit(100);

        String response = userService.resetDeposit(mockedUser.getId());
        String expected = "User with ID %s has been reset successfully".formatted(mockedUser.getId());

        assertEquals(expected, response);
    }
}
