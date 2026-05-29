package com.banking.service;

import com.banking.dto.request.AuthRequest;
import com.banking.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse authenticate(AuthRequest request);
}
