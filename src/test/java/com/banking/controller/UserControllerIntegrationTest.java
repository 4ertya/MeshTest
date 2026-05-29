package com.banking.controller;

import com.banking.dto.request.AuthRequest;
import com.banking.dto.request.AddEmailRequest;
import com.banking.repository.UserRepository;
import com.banking.repository.AccountRepository;
import com.banking.repository.EmailDataRepository;
import com.banking.repository.PhoneDataRepository;
import com.banking.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("banking_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailDataRepository emailDataRepository;

    @Autowired
    private PhoneDataRepository phoneDataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testUserId;
    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .name("Test User")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .password(passwordEncoder.encode("password123"))
                .build();
        user = userRepository.save(user);
        testUserId = user.getId();

        Account account = Account.builder()
                .user(user)
                .balance(new BigDecimal("1000.00"))
                .initialBalance(new BigDecimal("1000.00"))
                .build();
        accountRepository.save(account);

        EmailData email = EmailData.builder()
                .user(user)
                .email("test@example.com")
                .build();
        emailDataRepository.save(email);

        PhoneData phone = PhoneData.builder()
                .user(user)
                .phone("79001234567")
                .build();
        phoneDataRepository.save(phone);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    @DisplayName("GET /api/users/{id} returns user with correct data")
    void getUser_returnsUserData() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUserId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.emails", hasItem("test@example.com")))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @DisplayName("GET /api/users/search returns paginated results")
    void searchUsers_returnsPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("name", "Test")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("POST /api/users/me/emails adds a new email")
    void addEmail_success() throws Exception {
        AddEmailRequest request = new AddEmailRequest();
        request.setEmail("new@example.com");

        mockMvc.perform(post("/api/users/me/emails")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/users/me/emails fails with duplicate email")
    void addEmail_duplicate_fails() throws Exception {
        AddEmailRequest request = new AddEmailRequest();
        request.setEmail("test@example.com"); // Already exists

        mockMvc.perform(post("/api/users/me/emails")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request without JWT token returns 403")
    void request_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUserId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/users/search with dateOfBirth filter works correctly")
    void searchUsers_dateOfBirthFilter() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("dateOfBirth", "01.01.1980")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
