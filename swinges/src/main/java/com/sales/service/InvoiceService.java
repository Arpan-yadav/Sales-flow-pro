package com.sales.service;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.sales.model.*;
import com.sales.repository.InvoiceRepository;
import com.sales.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, OrderRepository orderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
    }

    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    public Invoice getById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
    }

    @Transactional
    public Invoice generate(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getInvoice() != null) return order.getInvoice();

        Invoice inv = new Invoice();
        inv.setOrder(order);
        inv.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        inv.setIssuedAt(LocalDateTime.now());
        return invoiceRepository.save(inv);
    }

    public byte[] download(Long id) {
        Invoice inv = getById(id);
        return generatePdf(inv.getOrder());
    }

    public byte[] generatePdf(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("INVOICE")
                .setBold().setFontSize(24).setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph("SalesFlow Pro System").setBold().setFontSize(14));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

            document.add(new Paragraph("\nCUSTOMER:").setBold());
            document.add(new Paragraph(order.getCustomer().getName()));
            document.add(new Paragraph(order.getCustomer().getEmail()));
            document.add(new Paragraph("\n"));

            Table table = new Table(4);
            table.addHeaderCell("Product");
            table.addHeaderCell("Qty");
            table.addHeaderCell("Price");
            table.addHeaderCell("Total");

            for (OrderItem item : order.getItems()) {
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("Rs. " + item.getUnitPrice());
                table.addCell("Rs. " + item.getLineTotal());
            }
            document.add(table);

            document.add(new Paragraph("\nSubtotal: Rs. " + order.getSubtotal()).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Discount: Rs. " + order.getDiscountAmount()).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Tax: Rs. " + order.getTaxAmount()).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("TOTAL: Rs. " + order.getTotal()).setBold().setFontSize(16).setTextAlignment(TextAlignment.RIGHT));

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF invoice", e);
        }
        return out.toByteArray();
    }
}
