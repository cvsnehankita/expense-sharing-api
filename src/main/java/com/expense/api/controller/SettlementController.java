package com.expense.api.controller;

import com.expense.api.entity.Settlement;
import com.expense.api.entity.User;
import com.expense.api.service.SettlementService;
import com.expense.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> settlePayment(
            @RequestParam Integer receiverId,
            @RequestParam Double amount,
            @RequestParam(required = false) Integer groupId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate amount
        if (amount <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Amount must be greater than 0"));
        }

        // Payer is always the current user
        try {
            Settlement settlement = settlementService.settlePayment(
                    currentUser.getUserId(),
                    receiverId,
                    amount,
                    groupId
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(settlement);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserSettlements(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(settlementService.getUserSettlements(currentUser.getUserId()));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupSettlements(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // This could be enhanced to check if user is a member of the group
        return ResponseEntity.ok(settlementService.getGroupSettlements(groupId));
    }
}
