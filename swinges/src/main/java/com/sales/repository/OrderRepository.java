package com.sales.repository;

import com.sales.model.Order;
import com.sales.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findBySalespersonId(Long salespersonId);
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    List<Order> findByDateRange(LocalDateTime from, LocalDateTime to);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to")
    List<Order> findByStatusAndDateRange(OrderStatus status, LocalDateTime from, LocalDateTime to);

    // Dashboard queries
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :from AND :to")
    java.math.BigDecimal sumRevenueBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    Long countOrdersBetween(LocalDateTime from, LocalDateTime to);
}
