package com.expense.api.service;

import com.expense.api.entity.Expense;
import com.expense.api.entity.ExpenseShare;
import com.expense.api.entity.Group;
import com.expense.api.entity.User;
import com.expense.api.repository.ExpenseRepository;
import com.expense.api.repository.ExpenseShareRepository;
import com.expense.api.repository.GroupRepository;
import com.expense.api.repository.SettlementRepository;
import com.expense.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final SettlementRepository settlementRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Expense addExpense(Integer groupId, Integer createdById, Double amount,
                              String description, List<Integer> involvedUserIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate that all involved users are members of the group
        for (Integer userId : involvedUserIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            if (!group.isMember(user)) {
                throw new RuntimeException("User " + userId + " is not a member of the group");
            }
        }
        Expense expense = Expense.builder()
                .group(group)
                .createdBy(createdBy)
                .amount(amount)
                .description(description)
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        // Split amount equally using BigDecimal for precision
        BigDecimal totalAmount = BigDecimal.valueOf(amount);
        BigDecimal numUsers = BigDecimal.valueOf(involvedUserIds.size());
        BigDecimal splitAmount = totalAmount.divide(numUsers, 2, RoundingMode.HALF_UP);

        BigDecimal allocated = BigDecimal.ZERO;

        for (int i = 0; i < involvedUserIds.size(); i++) {
            Integer userId = involvedUserIds.get(i);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Double shareAmount;
            if (i == involvedUserIds.size() - 1) {
                // Last user gets the remaining amount to handle rounding
                shareAmount = totalAmount.subtract(allocated).doubleValue();
            } else {
                shareAmount = splitAmount.doubleValue();
                allocated = allocated.add(splitAmount);
            }

            ExpenseShare share = ExpenseShare.builder()
                    .expense(savedExpense)
                    .user(user)
                    .amount(shareAmount)
                    .build();
            expenseShareRepository.save(share);

        }
        return savedExpense;
    }

    public List<Expense> getGroupExpenses(Integer groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return expenseRepository.findByGroup(group);
    }

    public Expense getExpenseById(Integer expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    @Transactional
    public void deleteExpense(Integer expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // Delete associated shares first (cascade should handle this, but being explicit)
        expenseShareRepository.deleteAll(expense.getShares());
        expenseRepository.delete(expense);
    }

    /**
     * Calculate balances for a group
     * Positive balance means the user is owed money
     * Negative balance means the user owes money
     */
    public Map<String, Double> calculateGroupBalances(Integer groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Map<Integer, BigDecimal> balances = new HashMap<>();

        // Initialize balances for all group members
        for (User member : group.getMembers()) {
            balances.put(member.getUserId(), BigDecimal.ZERO);
        }

        // Process all expenses
        List<Expense> expenses = expenseRepository.findByGroup(group);

        for (Expense expense : expenses) {
            Integer payerId = expense.getCreatedBy().getUserId();

            // Payer gets credited the full amount
            balances.put(payerId,
                    balances.get(payerId).add(BigDecimal.valueOf(expense.getAmount())));

            // Each share holder gets debited their share
            for (ExpenseShare share : expense.getShares()) {
                Integer userId = share.getUser().getUserId();
                balances.put(userId,
                        balances.get(userId).subtract(BigDecimal.valueOf(share.getAmount())));
            }
        }
// Subtract settlements
        settlementRepository.findAll().stream()
                .filter(s -> balances.containsKey(s.getPayer().getUserId()) &&
                        balances.containsKey(s.getReceiver().getUserId()))
                .forEach(settlement -> {
                    Integer payerId = settlement.getPayer().getUserId();
                    Integer receiverId = settlement.getReceiver().getUserId();
                    BigDecimal amount = BigDecimal.valueOf(settlement.getAmount());

                    balances.put(payerId, balances.get(payerId).subtract(amount));
                    balances.put(receiverId, balances.get(receiverId).add(amount));
                });

        // Convert to result map with user names
        Map<String, Double> result = new HashMap<>();
        for (User member : group.getMembers()) {
            BigDecimal balance = balances.get(member.getUserId());
            result.put(member.getUserName(),
                    balance.setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

        return result;
    }
}
