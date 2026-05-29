package com.banking.service;

import com.banking.dto.request.*;
import com.banking.dto.response.PageResponse;
import com.banking.dto.response.UserResponse;

public interface UserService {

    UserResponse getUserById(Long id);

    PageResponse<UserResponse> searchUsers(UserSearchRequest request);

    void addEmail(Long currentUserId, AddEmailRequest request);

    void updateEmail(Long currentUserId, Long emailId, UpdateEmailRequest request);

    void deleteEmail(Long currentUserId, Long emailId);

    void addPhone(Long currentUserId, AddPhoneRequest request);

    void updatePhone(Long currentUserId, Long phoneId, UpdatePhoneRequest request);

    void deletePhone(Long currentUserId, Long phoneId);
}
