package com.expense.api.service;

import com.expense.api.entity.Expense;
import com.expense.api.entity.ExpenseShare;
import com.expense.api.entity.Group;
import com.expense.api.entity.User;
import com.expense.api.repository.ExpenseRepository;
import com.expense.api.repository.ExpenseShareRepository;
import com.expense.api.repository.GroupRepository;
import com.expense.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public Expense addExpense(Integer groupId, Integer createdById, double amount, List<Integer> involvedUserIds) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        User createdBy = userRepository.findById(createdById).orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = Expense.builder()
                .group(group)
                .createdBy(createdBy)
                .amount(amount)
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        // Split amount equally and save shares
        double splitAmount = amount / involvedUserIds.size();

        for (Integer userId : involvedUserIds) {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            ExpenseShare share = ExpenseShare.builder()
                    .expense(savedExpense)
                    .user(user)
                    .amount(splitAmount)
                    .build();
            expenseShareRepository.save(share);
        }

        return savedExpense;
    }

    public List<Expense> getGroupExpenses(Integer groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        return expenseRepository.findByGroup(group);
    }
}
