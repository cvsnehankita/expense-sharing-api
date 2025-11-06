package com.expense.api.controller;

import com.expense.api.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.expense.api.entity.Expense;

import java.util.List;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public Expense addExpense(@RequestParam Integer groupId,
                              @RequestParam Integer createdBy,
                              @RequestParam double amount,
                              @RequestParam List<Integer> userIds) {
        return expenseService.addExpense(groupId, createdBy, amount, userIds);
    }

    @GetMapping("/group/{groupId}")
    public List<Expense> getGroupExpenses(@PathVariable Integer groupId) {
        return expenseService.getGroupExpenses(groupId);
    }
}
