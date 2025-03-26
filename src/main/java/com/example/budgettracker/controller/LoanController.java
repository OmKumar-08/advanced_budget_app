package com.example.budgettracker.controller;

import com.example.budgettracker.domain.Loan;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<Loan> createLoan(
            @AuthenticationPrincipal User user,
            @RequestBody Loan loan) {
        loan.setBorrower(user);
        return ResponseEntity.ok(loanService.createLoan(loan));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Loan> approveLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.approveLoan(id));
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<Void> recordLoanPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        loanService.recordLoanPayment(id, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<Loan>> getUserLoans(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loanService.getUserLoans(user));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Loan>> getUpcomingPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(loanService.getUpcomingPayments(startDate, endDate));
    }
}