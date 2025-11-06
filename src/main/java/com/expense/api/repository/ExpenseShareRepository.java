package com.expense.api.repository;

import com.expense.api.entity.ExpenseShare;
import com.expense.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Integer> {
    List<ExpenseShare> findByUser(User user);
}
