package com.example.budgettracker.service;

import com.example.budgettracker.domain.Loan;
import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final TransactionService transactionService;

    @Transactional
    public Loan createLoan(Loan loan) {
        loan.setRemainingAmount(loan.getPrincipalAmount());
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        
        if (loan.getStatus() != Loan.LoanStatus.PENDING) {
            throw new IllegalStateException("Loan is not in PENDING status");
        }

        loan.setStatus(Loan.LoanStatus.ACTIVE);
        
        // Create initial loan disbursement transaction
        Transaction disbursement = new Transaction();
        disbursement.setUser(loan.getBorrower());
        disbursement.setAmount(loan.getPrincipalAmount());
        disbursement.setType(Transaction.TransactionType.LOAN);
        disbursement.setDescription("Loan disbursement from " + loan.getLender().getUsername());
        disbursement.setTransactionDate(LocalDateTime.now());
        transactionService.createTransaction(disbursement);

        return loanRepository.save(loan);
    }

    @Transactional
    public void recordLoanPayment(Long loanId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan is not active");
        }

        // Create loan payment transaction
        Transaction payment = new Transaction();
        payment.setUser(loan.getBorrower());
        payment.setAmount(amount);
        payment.setType(Transaction.TransactionType.LOAN_PAYMENT);
        payment.setDescription("Loan payment to " + loan.getLender().getUsername());
        payment.setTransactionDate(LocalDateTime.now());
        transactionService.createTransaction(payment);

        // Update remaining amount
        loan.setRemainingAmount(loan.getRemainingAmount().subtract(amount));
        
        // Check if loan is fully paid
        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(Loan.LoanStatus.COMPLETED);
        }

        loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public List<Loan> getUserLoans(User user) {
        return loanRepository.findUserLoans(user);
    }

    @Transactional(readOnly = true)
    public List<Loan> getUpcomingPayments(LocalDateTime startDate, LocalDateTime endDate) {
        return loanRepository.findUpcomingPayments(startDate, endDate);
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void checkOverdueLoans() {
        LocalDateTime now = LocalDateTime.now();
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(now);

        for (Loan loan : overdueLoans) {
            if (loan.getStatus() == Loan.LoanStatus.ACTIVE) {
                loan.setStatus(Loan.LoanStatus.DEFAULTED);
                loanRepository.save(loan);
            }
        }
    }
}