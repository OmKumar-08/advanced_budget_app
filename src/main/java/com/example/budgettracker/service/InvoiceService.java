package com.example.budgettracker.service;

import com.example.budgettracker.domain.Invoice;
import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        invoice.setIssueDate(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice updateInvoice(Long id, Invoice updatedInvoice) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setDescription(updatedInvoice.getDescription());
        invoice.setDueDate(updatedInvoice.getDueDate());
        invoice.setPaymentTerms(updatedInvoice.getPaymentTerms());
        invoice.setPaymentMethod(updatedInvoice.getPaymentMethod());
        invoice.setAttachmentUrl(updatedInvoice.getAttachmentUrl());

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice markAsPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaymentDate(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice cancelInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByTransaction(Transaction transaction) {
        return invoiceRepository.findByTransaction(transaction);
    }

    @Transactional(readOnly = true)
    public List<Invoice> getPendingInvoices() {
        return invoiceRepository.findByStatus(Invoice.InvoiceStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findByStatus(Invoice.InvoiceStatus.OVERDUE);
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void checkOverdueInvoices() {
        LocalDateTime now = LocalDateTime.now();
        List<Invoice> overdueInvoices = invoiceRepository.findByDueDateBeforeAndStatus(
                now, Invoice.InvoiceStatus.PENDING);

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(Invoice.InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Run daily at 9 AM
    @Transactional
    public void sendPaymentReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Invoice> dueInvoices = invoiceRepository.findUpcomingDueInvoices(now);

        for (Invoice invoice : dueInvoices) {
            if (!invoice.isReminderSent()) {
                // TODO: Send reminder notification to user
                invoice.setReminderSent(true);
                invoice.setLastReminderDate(now);
                invoiceRepository.save(invoice);
            }
        }
    }
}