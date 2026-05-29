package com.banking.service;

import com.banking.config.CacheConfig;
import com.banking.dto.request.AddEmailRequest;
import com.banking.dto.request.AddPhoneRequest;
import com.banking.dto.request.UpdateEmailRequest;
import com.banking.entity.Account;
import com.banking.entity.EmailData;
import com.banking.entity.PhoneData;
import com.banking.entity.User;
import com.banking.exception.AccessDeniedException;
import com.banking.exception.BusinessException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.mapper.UserMapper;
import com.banking.repository.EmailDataRepository;
import com.banking.repository.PhoneDataRepository;
import com.banking.repository.UserRepository;
import com.banking.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailDataRepository emailDataRepository;
    @Mock
    private PhoneDataRepository phoneDataRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private EmailData testEmail;
    private PhoneData testPhone;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .password("hashedpass")
                .build();

        Account account = Account.builder()
                .id(1L).user(testUser)
                .balance(BigDecimal.valueOf(1000))
                .initialBalance(BigDecimal.valueOf(1000))
                .build();
        testUser.setAccount(account);

        testEmail = EmailData.builder().id(10L).user(testUser).email("test@example.com").build();
        testPhone = PhoneData.builder().id(20L).user(testUser).phone("79001234567").build();
    }

    @Test
    @DisplayName("addEmail succeeds when email is not taken")
    void addEmail_success() {
        when(emailDataRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        AddEmailRequest request = new AddEmailRequest();
        request.setEmail("new@example.com");

        userService.addEmail(1L, request);

        verify(emailDataRepository).save(argThat(e -> e.getEmail().equals("new@example.com")));
    }

    @Test
    @DisplayName("addEmail throws BusinessException when email already taken")
    void addEmail_taken_throws() {
        when(emailDataRepository.existsByEmail("taken@example.com")).thenReturn(true);

        AddEmailRequest request = new AddEmailRequest();
        request.setEmail("taken@example.com");

        assertThatThrownBy(() -> userService.addEmail(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already taken");

        verify(emailDataRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteEmail throws BusinessException when deleting last email")
    void deleteEmail_lastOne_throws() {
        when(emailDataRepository.findById(10L)).thenReturn(Optional.of(testEmail));
        when(emailDataRepository.countByUserId(1L)).thenReturn(1L);

        assertThatThrownBy(() -> userService.deleteEmail(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("last email");
    }

    @Test
    @DisplayName("deleteEmail throws AccessDeniedException when deleting another user's email")
    void deleteEmail_otherUser_throws() {
        User other = User.builder().id(99L).build();
        EmailData otherEmail = EmailData.builder().id(10L).user(other).email("other@example.com").build();
        when(emailDataRepository.findById(10L)).thenReturn(Optional.of(otherEmail));

        assertThatThrownBy(() -> userService.deleteEmail(1L, 10L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("updateEmail throws ResourceNotFoundException when email id not found")
    void updateEmail_notFound_throws() {
        when(emailDataRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateEmailRequest request = new UpdateEmailRequest();
        request.setNewEmail("new@example.com");

        assertThatThrownBy(() -> userService.updateEmail(1L, 999L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("addPhone succeeds when phone is not taken")
    void addPhone_success() {
        when(phoneDataRepository.existsByPhone("79009999999")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        AddPhoneRequest request = new AddPhoneRequest();
        request.setPhone("79009999999");

        userService.addPhone(1L, request);

        verify(phoneDataRepository).save(argThat(p -> p.getPhone().equals("79009999999")));
    }

    @Test
    @DisplayName("deletePhone throws BusinessException when deleting last phone")
    void deletePhone_lastOne_throws() {
        when(phoneDataRepository.findById(20L)).thenReturn(Optional.of(testPhone));
        when(phoneDataRepository.countByUserId(1L)).thenReturn(1L);

        assertThatThrownBy(() -> userService.deletePhone(1L, 20L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("last phone");
    }
}
