package com.expense.api.repository;

import com.expense.api.entity.Settlement;
import com.expense.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
    List<Settlement> findByPayerOrReceiver(User payer, User receiver);
}
