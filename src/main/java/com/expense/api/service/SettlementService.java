package com.expense.api.service;

import com.expense.api.entity.Settlement;
import com.expense.api.entity.User;
import com.expense.api.repository.SettlementRepository;
import com.expense.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;

    @Transactional
    public Settlement settlePayment(Integer payerId, Integer receiverId, double amount) {
        User payer = userRepository.findById(payerId).orElseThrow(() -> new RuntimeException("Payer not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Settlement settlement = Settlement.builder()
                .payer(payer)
                .receiver(receiver)
                .amount(amount)
                .expense(null)
                .build();

        return settlementRepository.save(settlement);
    }
}
