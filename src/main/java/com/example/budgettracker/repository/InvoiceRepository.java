package com.example.budgettracker.repository;

import com.example.budgettracker.domain.Invoice;
import com.example.budgettracker.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByTransaction(Transaction transaction);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    List<Invoice> findByDueDateBeforeAndStatus(LocalDateTime dueDate, Invoice.InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate <= :date AND i.status = 'PENDING' AND i.reminderSent = false")
    List<Invoice> findUpcomingDueInvoices(@Param("date") LocalDateTime date);
}