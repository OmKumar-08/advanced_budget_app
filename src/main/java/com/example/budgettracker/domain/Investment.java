package com.example.budgettracker.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "investments")
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal investedAmount;

    @Column(name = "current_value")
    private BigDecimal currentValue;

    @Column(name = "return_amount")
    private BigDecimal returnAmount;

    @Column(name = "return_percentage")
    private BigDecimal returnPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentStatus status;

    @Column(name = "investment_date", nullable = false)
    private LocalDateTime investmentDate;

    @Column(name = "maturity_date")
    private LocalDateTime maturityDate;

    @Column(name = "last_valuation_date")
    private LocalDateTime lastValuationDate;

    private String description;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum InvestmentType {
        STOCKS, BONDS, MUTUAL_FUNDS, REAL_ESTATE, CRYPTOCURRENCY, FIXED_DEPOSIT, OTHER
    }

    public enum InvestmentStatus {
        ACTIVE, SOLD, MATURED, CANCELLED
    }
}