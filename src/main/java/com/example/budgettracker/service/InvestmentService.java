package com.example.budgettracker.service;

import com.example.budgettracker.domain.Investment;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;

    @Transactional
    public Investment createInvestment(Investment investment) {
        investment.setCurrentValue(investment.getInvestedAmount());
        investment.setReturnAmount(BigDecimal.ZERO);
        investment.setReturnPercentage(BigDecimal.ZERO);
        investment.setStatus(Investment.InvestmentStatus.ACTIVE);
        investment.setLastValuationDate(LocalDateTime.now());
        return investmentRepository.save(investment);
    }

    @Transactional
    public Investment updateInvestment(Long id, Investment updatedInvestment) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found"));

        investment.setName(updatedInvestment.getName());
        investment.setDescription(updatedInvestment.getDescription());
        investment.setType(updatedInvestment.getType());
        investment.setRiskLevel(updatedInvestment.getRiskLevel());
        investment.setMaturityDate(updatedInvestment.getMaturityDate());

        return investmentRepository.save(investment);
    }

    @Transactional
    public Investment updateInvestmentValuation(Long id, BigDecimal currentValue) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found"));

        investment.setCurrentValue(currentValue);
        investment.setLastValuationDate(LocalDateTime.now());

        // Calculate returns
        BigDecimal returnAmount = currentValue.subtract(investment.getInvestedAmount());
        investment.setReturnAmount(returnAmount);

        // Calculate return percentage
        BigDecimal returnPercentage = returnAmount
                .multiply(new BigDecimal(100))
                .divide(investment.getInvestedAmount(), 2, RoundingMode.HALF_UP);
        investment.setReturnPercentage(returnPercentage);

        return investmentRepository.save(investment);
    }

    @Transactional(readOnly = true)
    public List<Investment> getUserInvestments(User user) {
        return investmentRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Investment> getUserInvestmentsByType(User user, Investment.InvestmentType type) {
        return investmentRepository.findByUserAndType(user, type);
    }

    @Transactional(readOnly = true)
    public List<Investment> getInvestmentPerformance(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return investmentRepository.findByUserAndLastValuationDateBetween(user, startDate, endDate);
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void checkMaturedInvestments() {
        LocalDateTime now = LocalDateTime.now();
        List<Investment> maturedInvestments = investmentRepository.findByMaturityDateBeforeAndStatus(
                now, Investment.InvestmentStatus.ACTIVE);

        for (Investment investment : maturedInvestments) {
            investment.setStatus(Investment.InvestmentStatus.MATURED);
            investmentRepository.save(investment);
        }
    }
}