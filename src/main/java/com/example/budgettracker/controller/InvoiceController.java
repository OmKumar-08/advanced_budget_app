package com.example.budgettracker.controller;

import com.example.budgettracker.domain.Invoice;
import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(
            @PathVariable Long id,
            @RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoice));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<Invoice>> getInvoicesByTransaction(
            @PathVariable Long transactionId) {
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        return ResponseEntity.ok(invoiceService.getInvoicesByTransaction(transaction));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Invoice>> getPendingInvoices() {
        return ResponseEntity.ok(invoiceService.getPendingInvoices());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Invoice>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Invoice> markInvoiceAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Invoice> cancelInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.cancelInvoice(id));
    }

    @PostMapping("/{id}/attachment")
    public ResponseEntity<Invoice> updateAttachment(
            @PathVariable Long id,
            @RequestBody AttachmentRequest request) {
        Invoice invoice = new Invoice();
        invoice.setAttachmentUrl(request.attachmentUrl());
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoice));
    }

    record AttachmentRequest(String attachmentUrl) {}
}