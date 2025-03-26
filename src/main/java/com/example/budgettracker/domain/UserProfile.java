package com.example.budgettracker.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String currency = "USD";

    @Column(name = "monthly_budget")
    private BigDecimal monthlyBudget = BigDecimal.ZERO;

    @Column(name = "savings_goal")
    private BigDecimal savingsGoal = BigDecimal.ZERO;

    @Column(name = "notification_email")
    private boolean emailNotificationsEnabled = true;

    @Column(name = "notification_push")
    private boolean pushNotificationsEnabled = true;

    @Column(name = "notification_sms")
    private boolean smsNotificationsEnabled = false;

    @Column(name = "budget_alert_threshold")
    private Integer budgetAlertThreshold = 80; // Percentage

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}