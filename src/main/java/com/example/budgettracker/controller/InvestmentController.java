package com.example.budgettracker.controller;

import com.example.budgettracker.domain.Investment;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
public class InvestmentController {
    private final InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<Investment> createInvestment(
            @AuthenticationPrincipal User user,
            @RequestBody Investment investment) {
        investment.setUser(user);
        return ResponseEntity.ok(investmentService.createInvestment(investment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investment> updateInvestment(
            @PathVariable Long id,
            @RequestBody Investment investment) {
        return ResponseEntity.ok(investmentService.updateInvestment(id, investment));
    }

    @GetMapping
    public ResponseEntity<List<Investment>> getUserInvestments(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(investmentService.getUserInvestments(user));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Investment>> getUserInvestmentsByType(
            @AuthenticationPrincipal User user,
            @PathVariable Investment.InvestmentType type) {
        return ResponseEntity.ok(investmentService.getUserInvestmentsByType(user, type));
    }

    @GetMapping("/performance")
    public ResponseEntity<List<Investment>> getInvestmentPerformance(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(investmentService.getInvestmentPerformance(user, startDate, endDate));
    }

    @PostMapping("/{id}/valuation")
    public ResponseEntity<Investment> updateInvestmentValuation(
            @PathVariable Long id,
            @RequestBody ValuationRequest request) {
        return ResponseEntity.ok(investmentService.updateInvestmentValuation(id, request.currentValue()));
    }

    record ValuationRequest(java.math.BigDecimal currentValue) {}
}