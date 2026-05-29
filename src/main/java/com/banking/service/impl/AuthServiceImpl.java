package com.banking.service.impl;

import com.banking.dto.request.AuthRequest;
import com.banking.dto.response.AuthResponse;
import com.banking.entity.User;
import com.banking.exception.BusinessException;
import com.banking.repository.UserRepository;
import com.banking.security.JwtTokenProvider;
import com.banking.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String INVALID_CREDENTIALS_MSG = "Invalid credentials";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (StringUtils.isAllBlank(request.getEmail(),request.getPhone())) {
            throw new BusinessException("Email or phone must be provided");
        }

        User user;
        if (StringUtils.isNotBlank(request.getEmail())) {
            log.debug("Authentication attempt via email={}", request.getEmail());
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException(INVALID_CREDENTIALS_MSG));
        } else {
            log.debug("Authentication attempt via phone={}", request.getPhone());
            user = userRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new BusinessException(INVALID_CREDENTIALS_MSG));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed authentication attempt for userId={}", user.getId());
            throw new BusinessException(INVALID_CREDENTIALS_MSG);
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        log.info("User authenticated successfully, userId={}", user.getId());
        return new AuthResponse(token, user.getId());
    }
}
