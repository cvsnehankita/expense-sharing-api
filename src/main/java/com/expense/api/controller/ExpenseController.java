package com.expense.api.controller;

import com.expense.api.entity.Expense;
import com.expense.api.entity.Group;
import com.expense.api.entity.User;
import com.expense.api.service.ExpenseService;
import com.expense.api.service.GroupService;
import com.expense.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;
    private final UserService userService;
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<?> addExpense(
            @RequestParam Integer groupId,
            @RequestParam Double amount,
            @RequestParam(required = false) String description,
            @RequestParam List<Integer> userIds,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.getGroupById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if current user is a member of the group
        if (!group.isMember(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not a member of this group"));
        }

        Expense expense = expenseService.addExpense(
                groupId,
                currentUser.getUserId(),
                amount,
                description,
                userIds
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupExpenses(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.getGroupById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if current user is a member of the group
        if (!group.isMember(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not a member of this group"));
        }

        List<Expense> expenses = expenseService.getGroupExpenses(groupId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/group/{groupId}/balances")
    public ResponseEntity<?> getGroupBalances(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.getGroupById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if current user is a member of the group
        if (!group.isMember(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not a member of this group"));
        }

        Map<String, Double> balances = expenseService.calculateGroupBalances(groupId);
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpense(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseService.getExpenseById(id);

        // Check if current user is a member of the expense's group
        if (!expense.getGroup().isMember(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to view this expense"));
        }

        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseService.getExpenseById(id);

        // Only the creator or an admin can delete an expense
        if (!expense.getCreatedBy().getUserId().equals(currentUser.getUserId()) &&
                !currentUser.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to delete this expense"));
        }

        expenseService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
    }
}