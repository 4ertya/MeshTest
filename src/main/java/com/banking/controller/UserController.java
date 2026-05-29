package com.banking.controller;

import com.banking.dto.request.*;
import com.banking.dto.response.PageResponse;
import com.banking.dto.response.UserResponse;
import com.banking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users with filters and pagination")
    public ResponseEntity<PageResponse<UserResponse>> searchUsers(
            @Valid UserSearchRequest request) {
        return ResponseEntity.ok(userService.searchUsers(request));
    }

    @PostMapping("/me/emails")
    @Operation(summary = "Add a new email to current user")
    public ResponseEntity<Void> addEmail(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddEmailRequest request) {
        log.debug("addEmail request from userId={}", userId);
        userService.addEmail(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/emails/{emailId}")
    @Operation(summary = "Update an existing email of current user")
    public ResponseEntity<Void> updateEmail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long emailId,
            @Valid @RequestBody UpdateEmailRequest request) {
        userService.updateEmail(userId, emailId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/emails/{emailId}")
    @Operation(summary = "Delete an email of current user")
    public ResponseEntity<Void> deleteEmail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long emailId) {
        userService.deleteEmail(userId, emailId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/phones")
    @Operation(summary = "Add a new phone to current user")
    public ResponseEntity<Void> addPhone(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddPhoneRequest request) {
        userService.addPhone(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/phones/{phoneId}")
    @Operation(summary = "Update an existing phone of current user")
    public ResponseEntity<Void> updatePhone(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long phoneId,
            @Valid @RequestBody UpdatePhoneRequest request) {
        userService.updatePhone(userId, phoneId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/phones/{phoneId}")
    @Operation(summary = "Delete a phone of current user")
    public ResponseEntity<Void> deletePhone(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long phoneId) {
        userService.deletePhone(userId, phoneId);
        return ResponseEntity.noContent().build();
    }
}
