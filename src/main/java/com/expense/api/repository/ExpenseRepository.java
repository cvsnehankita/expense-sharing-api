package com.expense.api.repository;

import com.expense.api.entity.Expense;
import com.expense.api.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Integer> {
    List<Expense> findByGroup(Group group);
}
