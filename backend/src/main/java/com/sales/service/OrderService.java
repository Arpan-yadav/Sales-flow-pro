package com.sales.service;

import com.sales.model.*;
import com.sales.model.enums.OrderStatus;
import com.sales.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository    orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository     userRepository;
    private final ProductRepository  productRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAll(OrderStatus status, Long customerId, Long salespersonId) {
        if (customerId != null)     return orderRepository.findByCustomerId(customerId);
        if (salespersonId != null)  return orderRepository.findBySalespersonId(salespersonId);
        if (status != null)         return orderRepository.findByStatus(status);
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    @Transactional
    public Order create(OrderCreateRequest req) {
        Customer customer = customerRepository.findById(req.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        User salesperson = userRepository.findById(req.getSalespersonId())
            .orElseThrow(() -> new RuntimeException("Salesperson not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setSalesperson(salesperson);
        order.setStatus(OrderStatus.PENDING);
        order.setDiscountPercent(nvl(req.getDiscountPercent()));
        order.setTaxPercent(nvl(req.getTaxPercent()));
        order.setNotes(req.getNotes());

        List<OrderItem> items = req.getItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));
            if (product.getStockQuantity() < itemReq.getQuantity())
                throw new RuntimeException("Insufficient stock for: " + product.getName()
                    + " (available: " + product.getStockQuantity() + ")");

            BigDecimal unitPrice = itemReq.getUnitPrice() != null ? itemReq.getUnitPrice() : product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(itemReq.getQuantity());
            oi.setUnitPrice(unitPrice);
            oi.setLineTotal(lineTotal);
            return oi;
        }).collect(Collectors.toList());

        order.setItems(items);
        recalculate(order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order order = getById(id);
        OrderStatus old = order.getStatus();

        if (newStatus == OrderStatus.CONFIRMED && old == OrderStatus.PENDING) {
            order.getItems().forEach(item -> {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() - item.getQuantity());
                productRepository.save(p);
            });
        }

        if (newStatus == OrderStatus.CANCELLED && old == OrderStatus.CONFIRMED) {
            order.getItems().forEach(item -> {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                productRepository.save(p);
            });
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    private void recalculate(Order order) {
        BigDecimal subtotal = order.getItems().stream()
            .map(OrderItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmt = subtotal
            .multiply(order.getDiscountPercent())
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal afterDiscount = subtotal.subtract(discountAmt);
        BigDecimal taxAmt = afterDiscount
            .multiply(order.getTaxPercent())
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal total = afterDiscount.add(taxAmt);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmt);
        order.setTaxAmount(taxAmt);
        order.setTotal(total);
    }

    private BigDecimal nvl(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    public static class OrderCreateRequest {
        private Long customerId;
        private Long salespersonId;
        private BigDecimal discountPercent;
        private BigDecimal taxPercent;
        private String notes;
        private List<ItemRequest> items;
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public Long getSalespersonId() { return salespersonId; }
        public void setSalespersonId(Long salespersonId) { this.salespersonId = salespersonId; }
        public BigDecimal getDiscountPercent() { return discountPercent; }
        public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }
        public BigDecimal getTaxPercent() { return taxPercent; }
        public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public List<ItemRequest> getItems() { return items; }
        public void setItems(List<ItemRequest> items) { this.items = items; }

        public static class ItemRequest {
            private Long productId;
            private Integer quantity;
            private BigDecimal unitPrice;
            public Long getProductId() { return productId; }
            public void setProductId(Long productId) { this.productId = productId; }
            public Integer getQuantity() { return quantity; }
            public void setQuantity(Integer quantity) { this.quantity = quantity; }
            public BigDecimal getUnitPrice() { return unitPrice; }
            public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        }
    }

    public static class StatusRequest {
        private OrderStatus status;
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }
}
