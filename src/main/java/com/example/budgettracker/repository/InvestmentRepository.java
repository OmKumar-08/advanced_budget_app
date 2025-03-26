package com.example.budgettracker.repository;

import com.example.budgettracker.domain.Investment;
import com.example.budgettracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByUser(User user);
    List<Investment> findByUserAndType(User user, Investment.InvestmentType type);
    List<Investment> findByUserAndLastValuationDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    List<Investment> findByMaturityDateBeforeAndStatus(LocalDateTime date, Investment.InvestmentStatus status);
}