package com.banking.service;

import com.banking.dto.request.TransferRequest;
import com.banking.entity.Account;
import com.banking.entity.User;
import com.banking.exception.BusinessException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        User userFrom = User.builder().id(1L).name("Alice").build();
        User userTo = User.builder().id(2L).name("Bob").build();

        fromAccount = Account.builder()
                .id(1L)
                .user(userFrom)
                .balance(new BigDecimal("1000.00"))
                .initialBalance(new BigDecimal("1000.00"))
                .build();

        toAccount = Account.builder()
                .id(2L)
                .user(userTo)
                .balance(new BigDecimal("500.00"))
                .initialBalance(new BigDecimal("500.00"))
                .build();
    }

    @Test
    @DisplayName("Successful transfer reduces sender balance and increases receiver balance")
    void transfer_success() {
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        TransferRequest request = new TransferRequest();
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("200.00"));

        transferService.transfer(1L, request);

        assertThat(fromAccount.getBalance()).isEqualByComparingTo("800.00");
        assertThat(toAccount.getBalance()).isEqualByComparingTo("700.00");
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    @DisplayName("Transfer fails when sender has insufficient funds")
    void transfer_insufficientFunds() {
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        TransferRequest request = new TransferRequest();
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("9999.00"));

        assertThatThrownBy(() -> transferService.transfer(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient funds");

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Transfer fails when transferring to yourself")
    void transfer_toSelf_fails() {
        TransferRequest request = new TransferRequest();
        request.setToUserId(1L);
        request.setAmount(new BigDecimal("100.00"));

        assertThatThrownBy(() -> transferService.transfer(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("yourself");

        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("Transfer fails when sender account not found")
    void transfer_senderNotFound() {
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest();
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("100.00"));

        assertThatThrownBy(() -> transferService.transfer(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Transfer of exact balance amount succeeds")
    void transfer_exactBalance_success() {
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        TransferRequest request = new TransferRequest();
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("1000.00"));

        transferService.transfer(1L, request);

        assertThat(fromAccount.getBalance()).isEqualByComparingTo("0.00");
        assertThat(toAccount.getBalance()).isEqualByComparingTo("1500.00");
    }
}
