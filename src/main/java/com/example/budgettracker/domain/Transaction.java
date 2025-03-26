package com.example.budgettracker.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "is_recurring")
    private boolean recurring = false;

    @Column(name = "recurrence_pattern")
    private String recurrencePattern;

    @Column(name = "next_recurrence_date")
    private LocalDateTime nextRecurrenceDate;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private Set<Settlement> settlements = new HashSet<>();

    @Column(name = "is_settled")
    private boolean settled = false;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TransactionType {
        EXPENSE, INCOME, LOAN, INVESTMENT, BILL_SPLIT , LOAN_PAYMENT
    }

    

    public enum TransactionCategory {
        FOOD, TRANSPORTATION, HOUSING, UTILITIES, ENTERTAINMENT,
        HEALTHCARE, EDUCATION, SHOPPING, INVESTMENT, LOAN_PAYMENT,
        SALARY, OTHER
    }

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    // Add getter and setter
    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}