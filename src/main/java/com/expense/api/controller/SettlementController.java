package com.expense.api.controller;

import com.expense.api.entity.Settlement;
import com.expense.api.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settlements")
public class SettlementController {
    private final SettlementService settlementService;
    @PostMapping
    public Settlement settlePayment(@RequestParam Integer payerId,
                                    @RequestParam Integer receiverId,
                                    @RequestParam double amount) {
        return settlementService.settlePayment(payerId, receiverId, amount);
    }
}
