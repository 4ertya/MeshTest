package com.banking.controller;

import com.banking.dto.request.TransferRequest;
import com.banking.service.TransferService;
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
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@Tag(name = "Transfer", description = "Money transfer between users")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Transfer money to another user")
    public ResponseEntity<Void> transfer(
            @AuthenticationPrincipal Long fromUserId,
            @Valid @RequestBody TransferRequest request) {
        log.info("Transfer request: fromUserId={}, toUserId={}, amount={}",
                fromUserId, request.getToUserId(), request.getAmount());
        transferService.transfer(fromUserId, request);
        return ResponseEntity.noContent().build();
    }
}
