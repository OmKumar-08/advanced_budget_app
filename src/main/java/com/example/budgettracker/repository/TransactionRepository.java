package com.example.budgettracker.repository;

import com.example.budgettracker.domain.Group;
import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    
    List<Transaction> findByUserAndType(User user, Transaction.TransactionType type);
    
    List<Transaction> findByUserAndCategory(User user, Transaction.TransactionCategory category);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserAndDateRange(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.recurring = true AND t.nextRecurrenceDate <= :currentDate")
    List<Transaction> findDueRecurringTransactions(LocalDateTime currentDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.group.id = :groupId")
    List<Transaction> findByGroupId(Long groupId);
    
    @Query("SELECT t FROM Transaction t WHERE t.settled = false AND t.group IS NOT NULL")
    List<Transaction> findUnsettledGroupTransactions();
    
    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.settlements WHERE t.id = :id")
    Transaction findByIdWithSettlements(Long id);
    List<Transaction> findByGroupAndType(Group group, Transaction.TransactionType type);
}
