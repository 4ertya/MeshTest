package com.banking.service.impl;

import com.banking.config.CacheConfig;
import com.banking.dto.request.*;
import com.banking.dto.response.PageResponse;
import com.banking.dto.response.UserResponse;
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
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final UserMapper userMapper;

    @Override
    @Cacheable(value = CacheConfig.USERS_CACHE, key = "#id")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by id={}", id);
        User user = findUserOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Cacheable(value = CacheConfig.USER_SEARCH_CACHE,
               key = "#request.dateOfBirth + '_' + #request.name + '_' + #request.email + '_' + #request.phone + '_' + #request.page + '_' + #request.size")
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> searchUsers(UserSearchRequest request) {
        log.debug("Searching users with filters: dateOfBirth={}, name={}, email={}, phone={}",
                request.getDateOfBirth(), request.getName(), request.getEmail(), request.getPhone());

        Page<User> page = userRepository.searchUsers(
                request.getDateOfBirth(),
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                PageRequest.of(request.getPage(), request.getSize())
        );
        return PageResponse.of(page.map(userMapper::toResponse));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USERS_CACHE, key = "#currentUserId")
    public void addEmail(Long currentUserId, AddEmailRequest request) {
        log.info("Adding email={} for userId={}", request.getEmail(), currentUserId);
        if (emailDataRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already taken");
        }
        User user = findUserOrThrow(currentUserId);
        EmailData emailData = EmailData.builder()
                .user(user)
                .email(request.getEmail())
                .build();
        emailDataRepository.save(emailData);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USERS_CACHE, key = "#currentUserId")
    public void updateEmail(Long currentUserId, Long emailId, UpdateEmailRequest request) {
        log.info("Updating emailId={} for userId={}", emailId, currentUserId);
        EmailData emailData = emailDataRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        if (!emailData.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only modify your own emails");
        }
        if (emailDataRepository.existsByEmail(request.getNewEmail())) {
            throw new BusinessException("Email is already taken");
        }
        emailData.setEmail(request.getNewEmail());
        emailDataRepository.save(emailData);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USERS_CACHE, key = "#currentUserId")
    public void deleteEmail(Long currentUserId, Long emailId) {
        log.info("Deleting emailId={} for userId={}", emailId, currentUserId);
        EmailData emailData = emailDataRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        if (!emailData.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only modify your own emails");
        }
        long count = emailDataRepository.countByUserId(currentUserId);
        if (count <= 1) {
            throw new BusinessException("Cannot delete the last email. User must have at least one email.");
        }
        emailDataRepository.delete(emailData);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USERS_CACHE, key = "#currentUserId")
    public void addPhone(Long currentUserId, AddPhoneRequest request) {
        log.info("Adding phone={} for userId={}", request.getPhone(), currentUserId);
        if (phoneDataRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Phone is already taken");
        }
        User user = findUserOrThrow(currentUserId);
        PhoneData phoneData = PhoneData.builder()
                .user(user)
                .phone(request.getPhone())
                .build();
        phoneDataRepository.save(phoneData);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USERS_CACHE, key = "#currentUserId")
    public void updatePhone(Long currentUserId, Long phoneId, UpdatePhoneRequest request) {
        log.info("Updating phoneId={} for userId={}", phoneId, currentUserId);
        PhoneData phoneData = phoneDataRepository.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));

        if (!phoneData.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only modify your own phones");
        }
        if (phoneDataRepository.existsByPhone(request.getNewPhone())) {
            throw new BusinessException("Phone is already taken");
        }
        phoneData.setPhone(request.getNewPhone());
        phoneDataRepository.save(phoneData);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USERS_CACHE, key = "#currentUserId")
    public void deletePhone(Long currentUserId, Long phoneId) {
        log.info("Deleting phoneId={} for userId={}", phoneId, currentUserId);
        PhoneData phoneData = phoneDataRepository.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));

        if (!phoneData.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only modify your own phones");
        }
        long count = phoneDataRepository.countByUserId(currentUserId);
        if (count <= 1) {
            throw new BusinessException("Cannot delete the last phone. User must have at least one phone.");
        }
        phoneDataRepository.delete(phoneData);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + id));
    }
}
