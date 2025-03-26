package com.example.budgettracker.service;

import com.example.budgettracker.domain.Settlement;
import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;

    @Transactional
    public Settlement createSettlement(Settlement settlement) {
        settlement.setStatus(Settlement.SettlementStatus.PENDING);
        return settlementRepository.save(settlement);
    }

    @Transactional
    public Settlement updateSettlementStatus(Long id, Settlement.SettlementStatus status, String paymentMethod, String paymentReference) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found"));

        settlement.setStatus(status);
        settlement.setPaymentMethod(paymentMethod);
        settlement.setPaymentReference(paymentReference);

        if (status == Settlement.SettlementStatus.COMPLETED) {
            settlement.setSettlementDate(LocalDateTime.now());
        }

        return settlementRepository.save(settlement);
    }

    @Transactional(readOnly = true)
    public List<Settlement> getUserSettlements(User user) {
        return settlementRepository.findAllUserSettlements(user);
    }

    @Transactional(readOnly = true)
    public List<Settlement> getSettlementsByStatus(Settlement.SettlementStatus status) {
        return settlementRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Settlement> getTransactionSettlements(Long transactionId) {
        return settlementRepository.findByTransactionId(transactionId);
    }

    @Scheduled(cron = "0 0 * * * *") // Run hourly
    @Transactional
    public void checkOverdueSettlements() {
        LocalDateTime now = LocalDateTime.now();
        List<Settlement> overdueSettlements = settlementRepository.findOverdueSettlements(now);

        for (Settlement settlement : overdueSettlements) {
            if (settlement.getStatus() == Settlement.SettlementStatus.PENDING) {
                settlement.setStatus(Settlement.SettlementStatus.OVERDUE);
                settlementRepository.save(settlement);
                // TODO: Send notification to users about overdue settlement
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Run daily at 9 AM
    @Transactional
    public void sendSettlementReminders() {
        LocalDateTime reminderDate = LocalDateTime.now().plusDays(2); // Send reminders 2 days before due date
        List<Settlement> settlementsNeedingReminders = settlementRepository.findSettlementsNeedingReminders(reminderDate);

        for (Settlement settlement : settlementsNeedingReminders) {
            settlement.setReminderSent(true);
            settlementRepository.save(settlement);
            
        }
    }

    @Transactional(readOnly = true)
    public boolean hasUnsettledPayments(Long transactionId) {
        return settlementRepository.hasUnsettledPayments(transactionId);
    }
}