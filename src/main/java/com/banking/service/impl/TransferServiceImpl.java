package com.banking.service.impl;

import com.banking.dto.request.TransferRequest;
import com.banking.entity.Account;
import com.banking.exception.BusinessException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(Long fromUserId, TransferRequest request) {
        Long toUserId = request.getToUserId();
        BigDecimal amount = request.getAmount();

        log.info("Transfer initiated: fromUserId={}, toUserId={}, amount={}", fromUserId, toUserId, amount);

        if (fromUserId.equals(toUserId)) {
            throw new BusinessException("Cannot transfer money to yourself");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transfer amount must be positive");
        }

        Account fromAccount;
        Account toAccount;

        if (fromUserId < toUserId) {
            fromAccount = getAccountForUpdate(fromUserId);
            toAccount = getAccountForUpdate(toUserId);
        } else {
            toAccount = getAccountForUpdate(toUserId);
            fromAccount = getAccountForUpdate(fromUserId);
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds: userId={}, balance={}, requested={}",
                    fromUserId, fromAccount.getBalance(), amount);
            throw new BusinessException("Insufficient funds. Available balance: " + fromAccount.getBalance());
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        log.info("Transfer completed: fromUserId={} newBalance={}, toUserId={} newBalance={}",
                fromUserId, fromAccount.getBalance(), toUserId, toAccount.getBalance());
    }

    private Account getAccountForUpdate(Long userId) {
        return accountRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for userId=" + userId));
    }
}
