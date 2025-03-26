package com.example.budgettracker.service;

import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.domain.Group;
import com.example.budgettracker.domain.Settlement;
import com.example.budgettracker.repository.TransactionRepository;
import com.example.budgettracker.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getGroup() != null) {
            createSettlementsForGroupTransaction(transaction);
        }
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setDescription(updatedTransaction.getDescription());
        transaction.setType(updatedTransaction.getType());
        transaction.setCategory(updatedTransaction.getCategory());
        transaction.setTransactionDate(updatedTransaction.getTransactionDate());
        
        if (transaction.isRecurring()) {
            transaction.setRecurrencePattern(updatedTransaction.getRecurrencePattern());
            transaction.setNextRecurrenceDate(updatedTransaction.getNextRecurrenceDate());
        }

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByType(User user, Transaction.TransactionType type) {
        return transactionRepository.findByUserAndType(user, type);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserAndDateRange(user, startDate, endDate);
    }

    @Transactional
    public void createSettlementsForGroupTransaction(Transaction transaction) {
        Group group = transaction.getGroup();
        Set<User> members = group.getMembers();
        BigDecimal splitAmount = transaction.getAmount().divide(BigDecimal.valueOf(members.size()));

        for (User member : members) {
            if (!member.equals(transaction.getUser())) {
                Settlement settlement = new Settlement();
                settlement.setTransaction(transaction);
                settlement.setPayer(member);
                settlement.setPayee(transaction.getUser());
                settlement.setAmount(splitAmount);
                settlement.setDueDate(transaction.getTransactionDate().plusDays(7));
                settlementRepository.save(settlement);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void processRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> dueTransactions = transactionRepository.findDueRecurringTransactions(now);

        for (Transaction transaction : dueTransactions) {
            Transaction newTransaction = new Transaction();
            newTransaction.setUser(transaction.getUser());
            newTransaction.setAmount(transaction.getAmount());
            newTransaction.setDescription(transaction.getDescription());
            newTransaction.setType(transaction.getType());
            newTransaction.setCategory(transaction.getCategory());
            newTransaction.setTransactionDate(now);
            newTransaction.setRecurring(true);
            newTransaction.setRecurrencePattern(transaction.getRecurrencePattern());
            // Calculate next recurrence date based on pattern
            newTransaction.setNextRecurrenceDate(calculateNextRecurrenceDate(transaction));

            transactionRepository.save(newTransaction);
        }
    }

    private LocalDateTime calculateNextRecurrenceDate(Transaction transaction) {
        // Implement logic to calculate next recurrence date based on pattern
        // Example: daily, weekly, monthly, etc.
        String pattern = transaction.getRecurrencePattern();
        LocalDateTime currentDate = transaction.getNextRecurrenceDate();

        return switch (pattern.toLowerCase()) {
            case "daily" -> currentDate.plusDays(1);
            case "weekly" -> currentDate.plusWeeks(1);
            case "monthly" -> currentDate.plusMonths(1);
            case "yearly" -> currentDate.plusYears(1);
            default -> currentDate.plusMonths(1); // Default to monthly
        };
    }

    @Transactional(readOnly = true)
    public List<Transaction> getUnsettledGroupTransactions() {
        return transactionRepository.findUnsettledGroupTransactions();
    }

    @Transactional
    public void markTransactionAsSettled(Long transactionId) {
        Transaction transaction = transactionRepository.findByIdWithSettlements(transactionId);
        if (transaction != null && !settlementRepository.hasUnsettledPayments(transactionId)) {
            transaction.setSettled(true);
            transactionRepository.save(transaction);
        }
    }
}