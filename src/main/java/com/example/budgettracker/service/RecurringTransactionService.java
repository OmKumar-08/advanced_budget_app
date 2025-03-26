package com.example.budgettracker.service;

import com.example.budgettracker.domain.RecurringTransaction;
import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.repository.RecurringTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionService transactionService;

    @Transactional
    public RecurringTransaction createRecurringTransaction(RecurringTransaction recurringTransaction) {
        recurringTransaction.setNextExecutionDate(calculateNextExecutionDate(recurringTransaction));
        return recurringTransactionRepository.save(recurringTransaction);
    }

    @Transactional
    public RecurringTransaction updateRecurringTransaction(Long id, RecurringTransaction updatedTransaction) {
        RecurringTransaction transaction = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));

        transaction.setTitle(updatedTransaction.getTitle());
        transaction.setDescription(updatedTransaction.getDescription());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setType(updatedTransaction.getType());
        transaction.setCategory(updatedTransaction.getCategory());
        transaction.setFrequency(updatedTransaction.getFrequency());
        transaction.setFrequencyInterval(updatedTransaction.getFrequencyInterval());
        transaction.setEndDate(updatedTransaction.getEndDate());
        transaction.setNotificationEnabled(updatedTransaction.isNotificationEnabled());
        transaction.setNotificationDaysBefore(updatedTransaction.getNotificationDaysBefore());

        return recurringTransactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<RecurringTransaction> getUserRecurringTransactions(User user) {
        return recurringTransactionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<RecurringTransaction> getActiveRecurringTransactions(User user) {
        return recurringTransactionRepository.findByUserAndActiveTrue(user);
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void processRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<RecurringTransaction> dueTransactions = recurringTransactionRepository
                .findByActiveAndNextExecutionDateBefore(true, now);

        for (RecurringTransaction recurringTransaction : dueTransactions) {
            // Create actual transaction
            Transaction transaction = new Transaction();
            transaction.setUser(recurringTransaction.getUser());
            transaction.setAmount(recurringTransaction.getAmount());
            transaction.setDescription(recurringTransaction.getTitle());
            transaction.setType(recurringTransaction.getType());
            transaction.setCategory(recurringTransaction.getCategory());
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setRecurring(true);
            transactionService.createTransaction(transaction);

            // Update next execution date
            recurringTransaction.setLastExecutionDate(now);
            recurringTransaction.setNextExecutionDate(calculateNextExecutionDate(recurringTransaction));

            // Check if recurring transaction should be deactivated
            if (recurringTransaction.getEndDate() != null && 
                recurringTransaction.getEndDate().isBefore(recurringTransaction.getNextExecutionDate())) {
                recurringTransaction.setActive(false);
            }

            recurringTransactionRepository.save(recurringTransaction);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Run daily at 9 AM
    @Transactional(readOnly = true)
    public void checkUpcomingRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<RecurringTransaction> upcomingTransactions = recurringTransactionRepository
                .findByActiveAndNotificationEnabled(true, true);

        for (RecurringTransaction transaction : upcomingTransactions) {
            if (transaction.getNotificationDaysBefore() != null) {
                LocalDateTime notificationDate = transaction.getNextExecutionDate()
                        .minusDays(transaction.getNotificationDaysBefore());
                
                if (now.isEqual(notificationDate) || now.isAfter(notificationDate)) {
                    // TODO: Send notification to user
                }
            }
        }
    }

    private LocalDateTime calculateNextExecutionDate(RecurringTransaction transaction) {
        LocalDateTime baseDate = transaction.getLastExecutionDate() != null ?
                transaction.getLastExecutionDate() : transaction.getStartDate();

        return switch (transaction.getFrequency()) {
            case DAILY -> baseDate.plusDays(transaction.getFrequencyInterval());
            case WEEKLY -> baseDate.plusWeeks(transaction.getFrequencyInterval());
            case MONTHLY -> baseDate.plusMonths(transaction.getFrequencyInterval());
            case YEARLY -> baseDate.plusYears(transaction.getFrequencyInterval());
        };
    }
}