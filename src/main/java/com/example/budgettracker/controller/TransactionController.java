package com.example.budgettracker.controller;

import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody Transaction transaction) {
        transaction.setUser(user);
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getUserTransactions(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.getUserTransactions(user));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transaction>> getUserTransactionsByType(
            @AuthenticationPrincipal User user,
            @PathVariable Transaction.TransactionType type) {
        return ResponseEntity.ok(transactionService.getUserTransactionsByType(user, type));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Transaction>> getUserTransactionsByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getUserTransactionsByDateRange(user, startDate, endDate));
    }

    @GetMapping("/unsettled-group")
    public ResponseEntity<List<Transaction>> getUnsettledGroupTransactions() {
        return ResponseEntity.ok(transactionService.getUnsettledGroupTransactions());
    }

    @PostMapping("/{id}/mark-settled")
    public ResponseEntity<Void> markTransactionAsSettled(@PathVariable Long id) {
        transactionService.markTransactionAsSettled(id);
        return ResponseEntity.ok().build();
    }
}