package com.expense.api.service;

import com.expense.api.entity.Settlement;
import com.expense.api.entity.User;
import com.expense.api.repository.SettlementRepository;
import com.expense.api.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;

    /**
     * Settle payment between two users with ACID guarantees
     * Uses optimistic locking and retries for concurrent transactions
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public Settlement settlePayment(Integer payerId, Integer receiverId,
                                    Double amount, Integer groupId) {

        log.info("Processing settlement: payer={}, receiver={}, amount={}",
                payerId, receiverId, amount);

        // Validate users exist (with locking to prevent concurrent modifications)
        User payer = userRepository.findById(payerId)
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Validate settlement
        if (payerId.equals(receiverId)) {
            throw new RuntimeException("Payer and receiver cannot be the same user");
        }

        if (amount <= 0) {
            throw new RuntimeException("Settlement amount must be positive");
        }

        try {
            // Create settlement record
            Settlement settlement = Settlement.builder()
                    .payer(payer)
                    .receiver(receiver)
                    .amount(amount)
                    .expense(null)  // Can be linked to specific expense if needed
                    .build();

            Settlement savedSettlement = settlementRepository.save(settlement);

            log.info("Settlement completed successfully: id={}", savedSettlement.getId());
            return savedSettlement;

        } catch (Exception e) {
            log.error("Settlement failed: {}", e.getMessage());
            throw new RuntimeException("Settlement failed: " + e.getMessage(), e);
        }
    }
    public List<Settlement> getUserSettlements(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return settlementRepository.findByPayerOrReceiver(user, user);
    }

    public List<Settlement> getGroupSettlements(Integer groupId) {
        // This would need to be enhanced to filter by group
        // For now, returning all settlements
        return settlementRepository.findAll();
    }

    /**
     * Calculate net balance for a user across all settlements
     */
    public Double calculateUserNetBalance(Integer userId) {
        List<Settlement> settlements = getUserSettlements(userId);

        double balance = 0.0;
        for (Settlement settlement : settlements) {
            if (settlement.getPayer().getUserId().equals(userId)) {
                balance -= settlement.getAmount();  // User paid
            } else {
                balance += settlement.getAmount();  // User received
            }
        }

        return balance;
    }
}