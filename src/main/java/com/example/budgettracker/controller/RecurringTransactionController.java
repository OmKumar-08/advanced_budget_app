package com.example.budgettracker.controller;

import com.example.budgettracker.domain.RecurringTransaction;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/recurring-transactions")
@RequiredArgsConstructor
public class RecurringTransactionController {
    private final RecurringTransactionService recurringTransactionService;

    @PostMapping
    public ResponseEntity<RecurringTransaction> createRecurringTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody RecurringTransaction transaction) {
        transaction.setUser(user);
        return ResponseEntity.ok(recurringTransactionService.createRecurringTransaction(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransaction> updateRecurringTransaction(
            @PathVariable Long id,
            @RequestBody RecurringTransaction transaction) {
        return ResponseEntity.ok(recurringTransactionService.updateRecurringTransaction(id, transaction));
    }

    @GetMapping
    public ResponseEntity<List<RecurringTransaction>> getUserRecurringTransactions(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recurringTransactionService.getUserRecurringTransactions(user));
    }

    @GetMapping("/active")
    public ResponseEntity<List<RecurringTransaction>> getActiveRecurringTransactions(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recurringTransactionService.getActiveRecurringTransactions(user));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<RecurringTransaction> deactivateRecurringTransaction(
            @PathVariable Long id) {
        RecurringTransaction transaction = recurringTransactionService.updateRecurringTransaction(id,
                new RecurringTransaction() {{ setActive(false); }});
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{id}/notification-preferences")
    public ResponseEntity<RecurringTransaction> updateNotificationPreferences(
            @PathVariable Long id,
            @RequestBody NotificationPreferences preferences) {
        RecurringTransaction transaction = recurringTransactionService.updateRecurringTransaction(id,
                new RecurringTransaction() {{
                    setNotificationEnabled(preferences.enabled());
                    setNotificationDaysBefore(preferences.daysBefore());
                }});
        return ResponseEntity.ok(transaction);
    }

    record NotificationPreferences(boolean enabled, Integer daysBefore) {}
}