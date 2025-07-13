package org.example.vendingmachine.api.v1.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.vendingmachine.TestHelper;

import org.example.vendingmachine.api.v1.dto.request.AuthRequestDto;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;

import org.example.vendingmachine.api.v1.model.TokenModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.UserRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private List<UserRequestDto> getUsersCreateRequestDto() {
        UserRequestDto buyerUserDto = TestHelper.getBuyerUserRequestDto();
        UserRequestDto sellerUserDto = TestHelper.getSellerUserRequestDto();

        return List.of(buyerUserDto, sellerUserDto);
    }


    private List<String> getCreateUsersPayload(List<UserRequestDto> usersRequestDto) throws JsonProcessingException {
        return usersRequestDto.stream().map(reqDto -> {
            try {
                return objectMapper.writeValueAsString(reqDto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private void createUsersAndAuthenticate() throws JsonProcessingException {
        List<UserRequestDto> usersRequest = getUsersCreateRequestDto();

        List<String> allUsersRequestJson = getCreateUsersPayload(usersRequest);

        allUsersRequestJson.forEach(requestJson -> {
            try {
                mockMvc
                        .perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        usersRequest.forEach(userRequestDto -> {
            AuthRequestDto authRequestDto = new AuthRequestDto(userRequestDto.getName(), userRequestDto.getPassword());
            String authRequestStr = "";
            try {
                authRequestStr = objectMapper.writeValueAsString(authRequestDto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                mockMvc
                        .perform(post("/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(authRequestStr))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Nested
    class CreateUserEndpointTest {
        @BeforeEach
        void setup() {
            userRepository.deleteAll();
        }

        @Test
        void createUser_ShouldReturnCreatedUser_WhenInputIsValid() throws Exception {
            List<UserRequestDto> usersRequest = getUsersCreateRequestDto();
            List<String> allUsersRequestJson = getCreateUsersPayload(usersRequest);

            allUsersRequestJson.forEach(requestJson -> {
                try {
                     mockMvc
                            .perform(post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJson))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.status").value("CREATED"))
                            .andExpect(jsonPath("$.data.user_id").exists())
                            .andExpect(jsonPath("$.errorMessage").value(IsNull.nullValue()));

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }


    @Nested
    class AuthenticatedUserEndpointsTest {
        @BeforeEach
        void setUp() throws Exception {
            userRepository.deleteAll();
            createUsersAndAuthenticate();
        }

        @Test
        void getUser_shouldReturnUnAuthorized_whenAnonymousUserCall() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            int firstUserId = firstUser.getId();
            mockMvc
                    .perform(get("/users/" + firstUserId))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getUser_shouldReturnForbidden_whenUserAccessAnotherResource() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";
            String sellerUserName = "Dummy_SELLER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();
            UserModel secondUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int secondUserId = secondUser.getId();

            mockMvc
                    .perform(get("/users/" + secondUserId)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getUser_shouldReturnUserInfo_whenUserAccessHisOwnResourceWithValidJwt() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";
            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> userTokens = firstUser.getTokens();
            int firstUserId = firstUser.getId();
            mockMvc
                    .perform(get("/users/" + firstUserId)
                            .header("Authorization", "Bearer " + userTokens
                                    .get(userTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isOk());
        }


        @Test
        void updateUser_shouldReturnUnAuthorized_whenAnonymousUserCall() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            int firstUserId = firstUser.getId();
            mockMvc
                    .perform(patch("/users/" + firstUserId))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void updateUser_shouldReturnForbidden_whenUserAccessAnotherResource() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";
            String sellerUserName = "Dummy_SELLER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();

            UserModel secondUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int secondUserId = secondUser.getId();

            String requestBody = objectMapper.writeValueAsString(new UserRequestDto());

            mockMvc
                    .perform(patch("/users/" + secondUserId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updateUser_shouldReturnOk_whenInputIsValid() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();
            int firstUserId = firstUser.getId();


            String requestBody = objectMapper.writeValueAsString(TestHelper.getBuyerUserRequestDto());

            mockMvc
                    .perform(patch("/users/" + firstUserId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteUser_shouldReturnUnAuthorized_whenAnonymousUserCall() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            int firstUserId = firstUser.getId();
            mockMvc
                    .perform(delete("/users/" + firstUserId))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteUser_shouldReturnForbidden_whenUserAccessAnotherResource() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";
            String sellerUserName = "Dummy_SELLER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();

            UserModel secondUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int secondUserId = secondUser.getId();

            String requestBody = objectMapper.writeValueAsString(new UserRequestDto());

            mockMvc
                    .perform(delete("/users/" + secondUserId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteUser_shouldReturnOk_whenInputIsValid() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();
            int firstUserId = firstUser.getId();

            String requestBody = objectMapper.writeValueAsString(TestHelper.getBuyerUserRequestDto());

            mockMvc
                    .perform(delete("/users/" + firstUserId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void addDeposit_shouldReturnUnAuthorized_whenAnonymousUserCall() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            int firstUserId = firstUser.getId();
            mockMvc
                    .perform(post("/users/" + firstUserId + "/deposit/10"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void addDeposit_shouldReturnForbidden_whenUserAccessAnotherResource() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";
            String sellerUserName = "Dummy_SELLER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();

            UserModel secondUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int secondUserId = secondUser.getId();

            String requestBody = objectMapper.writeValueAsString(new UserRequestDto());

            mockMvc
                    .perform(post("/users/" + secondUserId + "/deposit/10")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void addDeposit_shouldReturnOk_whenInputIsValid() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();
            int firstUserId = firstUser.getId();

            String requestBody = objectMapper.writeValueAsString(TestHelper.getBuyerUserRequestDto());

            mockMvc
                    .perform(post("/users/" + firstUserId + "/deposit/10")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void resetDeposit_shouldReturnUnAuthorized_whenAnonymousUserCall() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            int firstUserId = firstUser.getId();
            mockMvc
                    .perform(post("/users/" + firstUserId + "/reset-deposit"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void resetDeposit_shouldReturnForbidden_whenUserAccessAnotherResource() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";
            String sellerUserName = "Dummy_SELLER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();

            UserModel secondUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int secondUserId = secondUser.getId();

            String requestBody = objectMapper.writeValueAsString(new UserRequestDto());

            mockMvc
                    .perform(post("/users/" + secondUserId + "/reset-deposit")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void resetDeposit_shouldReturnOk_whenInputIsValid() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";

            UserModel firstUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> firstUserTokens = firstUser.getTokens();
            int firstUserId = firstUser.getId();

            String requestBody = objectMapper.writeValueAsString(TestHelper.getBuyerUserRequestDto());

            mockMvc
                    .perform(post("/users/" + firstUserId + "/reset-deposit")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + firstUserTokens
                                    .get(firstUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isOk());
        }
    }
}
