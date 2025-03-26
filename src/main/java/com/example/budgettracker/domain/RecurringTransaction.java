package com.example.budgettracker.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transaction.TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transaction.TransactionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrequencyType frequency;

    @Column(name = "frequency_interval", nullable = false)
    private Integer frequencyInterval;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "last_execution_date")
    private LocalDateTime lastExecutionDate;

    @Column(name = "next_execution_date")
    private LocalDateTime nextExecutionDate;

    @Column(name = "notification_enabled")
    private boolean notificationEnabled = true;

    @Column(name = "notification_days_before")
    private Integer notificationDaysBefore;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum FrequencyType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }
}