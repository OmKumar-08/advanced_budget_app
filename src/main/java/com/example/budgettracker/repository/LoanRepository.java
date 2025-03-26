package com.example.budgettracker.repository;

import com.example.budgettracker.domain.Loan;
import com.example.budgettracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBorrower(User borrower);
    List<Loan> findByLender(User lender);
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :now")
    List<Loan> findOverdueLoans(LocalDateTime now);
    
    @Query("SELECT l FROM Loan l WHERE l.borrower = :user OR l.lender = :user")
    List<Loan> findUserLoans(User user);
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate BETWEEN :startDate AND :endDate")
    List<Loan> findUpcomingPayments(LocalDateTime startDate, LocalDateTime endDate);
}