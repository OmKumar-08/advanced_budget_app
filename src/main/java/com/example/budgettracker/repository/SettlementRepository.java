package com.example.budgettracker.repository;

import com.example.budgettracker.domain.Settlement;
import com.example.budgettracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByPayer(User payer);
    
    List<Settlement> findByPayee(User payee);
    
    List<Settlement> findByStatus(Settlement.SettlementStatus status);
    
    @Query("SELECT s FROM Settlement s WHERE s.payer = :user OR s.payee = :user")
    List<Settlement> findAllUserSettlements(User user);
    
    @Query("SELECT s FROM Settlement s WHERE s.status = 'PENDING' AND s.dueDate <= :currentDate")
    List<Settlement> findOverdueSettlements(LocalDateTime currentDate);
    
    @Query("SELECT s FROM Settlement s WHERE s.transaction.id = :transactionId")
    List<Settlement> findByTransactionId(Long transactionId);
    
    @Query("SELECT s FROM Settlement s WHERE s.status = 'PENDING' AND s.reminderSent = false AND s.dueDate <= :reminderDate")
    List<Settlement> findSettlementsNeedingReminders(LocalDateTime reminderDate);
    
    @Query("SELECT COUNT(s) > 0 FROM Settlement s WHERE s.transaction.id = :transactionId AND s.status = 'PENDING'")
    boolean hasUnsettledPayments(Long transactionId);

    List<Settlement> findByTransactionIdIn(List<Long> transactionIds);
}