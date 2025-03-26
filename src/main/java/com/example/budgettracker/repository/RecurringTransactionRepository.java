package com.example.budgettracker.repository;

import com.example.budgettracker.domain.RecurringTransaction;
import com.example.budgettracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findByUser(User user);
    List<RecurringTransaction> findByUserAndActiveTrue(User user);
    List<RecurringTransaction> findByActiveAndNextExecutionDateBefore(boolean active, LocalDateTime date);
    List<RecurringTransaction> findByActiveAndNotificationEnabled(boolean active, boolean notificationEnabled);
}