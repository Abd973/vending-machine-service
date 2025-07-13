package org.example.vendingmachine.api.v1.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vendingmachine.TestHelper;
import org.example.vendingmachine.api.v1.dto.Role;
import org.example.vendingmachine.api.v1.dto.request.AuthRequestDto;
import org.example.vendingmachine.api.v1.dto.request.ProductRequestDto;
import org.example.vendingmachine.api.v1.dto.request.UserRequestDto;
import org.example.vendingmachine.api.v1.model.ProductModel;
import org.example.vendingmachine.api.v1.model.TokenModel;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.example.vendingmachine.api.v1.repository.ProductRepository;
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
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    void createProducts() {
        List<UserModel> allUsers = (List<UserModel>) userRepository.findAll();
        List<UserModel> allSellers = allUsers
                .stream()
                .filter(user -> user
                        .getRole()
                        .equals(Role.SELLER))
                .toList();

        allSellers.forEach(sellerUser -> {
            List<TokenModel> sellerUserTokens = sellerUser.getTokens();

            ProductRequestDto productRequestDto = TestHelper.getProductRequestDto(sellerUser.getId());
            String productPayload = null;
            try {
                productPayload = objectMapper.writeValueAsString(productRequestDto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                mockMvc
                        .perform(post("/products")
                                .content(productPayload)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + sellerUserTokens
                                        .get(sellerUserTokens.size() - 1)
                                        .getToken()))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<UserRequestDto> getUsersCreateRequestDto() {
        UserRequestDto buyerUserDto = TestHelper.getBuyerUserRequestDto();
        UserRequestDto sellerUserDto = TestHelper.getSellerUserRequestDto();
        UserRequestDto sellerUserDto2 = TestHelper.getSellerUserRequestDto();
        sellerUserDto2.setName(sellerUserDto2.getName() + "_2");

        return List.of(buyerUserDto, sellerUserDto, sellerUserDto2);
    }


    private List<String> getCreateUsersPayload(List<UserRequestDto> usersRequestDto) throws JsonProcessingException {
        return usersRequestDto
                .stream()
                .map(reqDto -> {
                    try {
                        return objectMapper.writeValueAsString(reqDto);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
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
    class CreateProductEndpointTest {
        @BeforeEach
        void setup() throws JsonProcessingException {
            productRepository.deleteAll();
            userRepository.deleteAll();
            createUsersAndAuthenticate();
        }

        @Test
        void createProduct_ShouldReturnCreatedProduct_WhenInputIsValid() throws Exception {
            String sellerUserName = "Dummy_SELLER_Name";
            UserModel sellerUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            List<TokenModel> sellerUserTokens = sellerUser.getTokens();

            ProductRequestDto productRequestDto = TestHelper.getProductRequestDto(sellerUser.getId());
            String productPayload = objectMapper.writeValueAsString(productRequestDto);

            mockMvc
                    .perform(post("/products")
                            .content(productPayload)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + sellerUserTokens
                                    .get(sellerUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isCreated());
        }

        @Test
        void createProduct_ShouldReturnForbidden_WhenUserRoleBuyer() throws Exception {
            String buyerUserName = "Dummy_BUYER_Name";
            UserModel buyerUser = userRepository
                    .findByName(buyerUserName)
                    .get();
            List<TokenModel> buyerUserTokens = buyerUser.getTokens();

            ProductRequestDto productRequestDto = TestHelper.getProductRequestDto(buyerUser.getId());
            String productPayload = objectMapper.writeValueAsString(productRequestDto);

            mockMvc
                    .perform(post("/products")
                            .content(productPayload)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + buyerUserTokens
                                    .get(buyerUserTokens.size() - 1)
                                    .getToken()))
                    .andExpect(status().isForbidden());

        }

    }

    @Nested
    class ProductEndpointsTest {
        @BeforeEach
        void setUp() throws Exception {
            userRepository.deleteAll();
            productRepository.deleteAll();
            createUsersAndAuthenticate();
            createProducts();
        }

        @Test
        void getProduct_shouldReturnUnAuthorized_whenNoJwtUsed() throws Exception {

            String sellerUserName = "Dummy_SELLER_Name";

            UserModel sellerUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int sellerUserId = sellerUser.getId();

            List<ProductModel> allProducts = (List<ProductModel>) productRepository.findAll();
            List<ProductModel> sellerProducts = allProducts
                    .stream()
                    .filter(product -> sellerUserId == product
                            .getSeller()
                            .getId())
                    .toList();

            int productId = sellerProducts
                    .get(0)
                    .getId();

            mockMvc
                    .perform(get("/products/" + productId))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getProduct_shouldReturnResponse_whenUserAccessAnotherResource() throws Exception {
            String sellerUserName = "Dummy_SELLER_Name";
            String sellerUserName2 = sellerUserName + "_2";

            UserModel sellerUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int sellerUserId = sellerUser.getId();

            UserModel sellerUser2 = userRepository
                    .findByName(sellerUserName2)
                    .get();
            List<TokenModel> sellerTokens2 = sellerUser2.getTokens();


            List<ProductModel> allProducts = (List<ProductModel>) productRepository.findAll();
            List<ProductModel> sellerProducts = allProducts
                    .stream()
                    .filter(product -> sellerUserId == product
                            .getSeller()
                            .getId())
                    .toList();

            int productId = sellerProducts
                    .get(0)
                    .getId();


            mockMvc
                    .perform(get("/products/" + productId).header("Authorization", "Bearer " + sellerTokens2
                            .get(sellerTokens2.size() - 1)
                            .getToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void getProduct_shouldReturnProductInfo_whenUserAccessHisOwnResourceWithValidJwt() throws Exception {
            String sellerUserName = "Dummy_SELLER_Name";

            UserModel sellerUser = userRepository
                    .findByName(sellerUserName)
                    .get();
            int sellerUserId = sellerUser.getId();
            List<TokenModel> sellerTokens = sellerUser.getTokens();

            List<ProductModel> allProducts = (List<ProductModel>) productRepository.findAll();
            List<ProductModel> sellerProducts = allProducts
                    .stream()
                    .filter(product -> sellerUserId == product
                            .getSeller()
                            .getId())
                    .toList();

            int productId = sellerProducts
                    .get(0)
                    .getId();

            mockMvc
                    .perform(get("/products/" + productId).header("Authorization", "Bearer " + sellerTokens
                            .get(sellerTokens.size() - 1)
                            .getToken()))
                    .andExpect(status().isOk());

        }

    }

}
