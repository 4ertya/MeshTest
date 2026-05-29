package com.banking.service;

import com.banking.dto.request.TransferRequest;

public interface TransferService {
    void transfer(Long fromUserId, TransferRequest request);
}
