package com.banking.scheduler;

import com.banking.entity.Account;
import com.banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BalanceAccrualScheduler {

    private static final BigDecimal INTEREST_RATE = new BigDecimal("1.10");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");

    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void accrueInterest() {
        log.debug("Running balance accrual job");
        List<Account> accounts = accountRepository.findAllAccounts();

        int updated = 0;
        for (Account account : accounts) {
            BigDecimal currentBalance = account.getBalance();
            BigDecimal maxBalance = account.getInitialBalance()
                    .multiply(MAX_MULTIPLIER)
                    .setScale(2, RoundingMode.HALF_DOWN);

            if (currentBalance.compareTo(maxBalance) >= 0) {
                continue;
            }

            BigDecimal newBalance = currentBalance.multiply(INTEREST_RATE)
                    .setScale(2, RoundingMode.HALF_DOWN);

            if (newBalance.compareTo(maxBalance) > 0) {
                newBalance = maxBalance;
            }

            account.setBalance(newBalance);
            accountRepository.save(account);
            updated++;
        }

        log.info("Balance accrual job completed. Updated {} accounts.", updated);
    }

    private final AccountRepository accountRepository;
}
