package com.banking.service;

import com.banking.entity.Account;
import com.banking.entity.User;
import com.banking.repository.AccountRepository;
import com.banking.scheduler.BalanceAccrualScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceAccrualSchedulerTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BalanceAccrualScheduler scheduler;

    private Account buildAccount(long userId, String balance, String initial) {
        User user = User.builder().id(userId).build();
        return Account.builder()
                .id(userId)
                .user(user)
                .balance(new BigDecimal(balance))
                .initialBalance(new BigDecimal(initial))
                .build();
    }

    @Test
    @DisplayName("Balance increases by 10% when below 207% cap")
    void accrueInterest_increasesBy10Percent() {
        Account account = buildAccount(1L, "100.00", "100.00");
        when(accountRepository.findAllAccounts()).thenReturn(List.of(account));

        scheduler.accrueInterest();

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getBalance()).isEqualByComparingTo("110.00");
    }

    @Test
    @DisplayName("Balance is capped at 207% of initial deposit")
    void accrueInterest_capsAt207Percent() {
        Account account = buildAccount(1L, "200.00", "100.00");
        when(accountRepository.findAllAccounts()).thenReturn(List.of(account));

        scheduler.accrueInterest();

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getBalance()).isEqualByComparingTo("207.00");
    }

    @Test
    @DisplayName("Balance at exact cap is not modified")
    void accrueInterest_atCap_skipped() {
        Account account = buildAccount(1L, "207.00", "100.00");
        when(accountRepository.findAllAccounts()).thenReturn(List.of(account));

        scheduler.accrueInterest();

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Multiple accounts updated independently")
    void accrueInterest_multipleAccounts() {
        Account a1 = buildAccount(1L, "100.00", "100.00");
        Account a2 = buildAccount(2L, "500.00", "500.00");
        Account a3 = buildAccount(3L, "1035.00", "500.00");

        when(accountRepository.findAllAccounts()).thenReturn(List.of(a1, a2, a3));

        scheduler.accrueInterest();

        verify(accountRepository, times(2)).save(any(Account.class));
        assertThat(a1.getBalance()).isEqualByComparingTo("110.00");
        assertThat(a2.getBalance()).isEqualByComparingTo("550.00");
        assertThat(a3.getBalance()).isEqualByComparingTo("1035.00");
    }
}
