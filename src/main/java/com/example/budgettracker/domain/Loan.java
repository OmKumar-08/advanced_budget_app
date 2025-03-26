package com.example.budgettracker.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "lender_id", nullable = false)
    private User lender;

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "payment_frequency")
    private String paymentFrequency; // WEEKLY, MONTHLY, etc.

    @Column(name = "remaining_amount")
    private BigDecimal remainingAmount;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private Set<Transaction> payments = new HashSet<>();

    // Add getter and setter
    public Set<Transaction> getPayments() {
        return payments;
    }

    public void setPayments(Set<Transaction> payments) {
        this.payments = payments;
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum LoanStatus {
        PENDING,   
        ACTIVE,     
        COMPLETED,  
        DEFAULTED,  
        CANCELLED   
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}