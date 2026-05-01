package com.sales.service;

import com.sales.model.enums.OrderStatus;
import com.sales.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final OrderRepository    orderRepository;
    private final ProductRepository  productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository     userRepository;

    public ReportService(OrderRepository orderRepository, ProductRepository productRepository, 
                         CustomerRepository customerRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> getDashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd   = todayStart.plusDays(1);
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        BigDecimal revenueToday = orderRepository.sumRevenueBetween(todayStart, todayEnd);
        BigDecimal revenueMonth = orderRepository.sumRevenueBetween(monthStart, LocalDateTime.now());

        LocalDateTime yestStart = todayStart.minusDays(1);
        BigDecimal revenueYesterday = orderRepository.sumRevenueBetween(yestStart, todayStart);
        double changePct = 0;
        if (revenueYesterday != null && revenueYesterday.doubleValue() > 0) {
            BigDecimal revToday = revenueToday != null ? revenueToday : BigDecimal.ZERO;
            changePct = ((revToday.doubleValue() - revenueYesterday.doubleValue()) / revenueYesterday.doubleValue()) * 100;
        }

        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            ordersByStatus.put(s.name(), (long) orderRepository.findByStatus(s).size());
        }

        Map<String, Object> dash = new LinkedHashMap<>();
        dash.put("revenueToday",      revenueToday != null ? revenueToday : BigDecimal.ZERO);
        dash.put("revenueMonth",      revenueMonth != null ? revenueMonth : BigDecimal.ZERO);
        dash.put("revenueChangePct",  Math.round(changePct * 10.0) / 10.0);
        dash.put("ordersTotal",       orderRepository.count());
        dash.put("customersTotal",    customerRepository.count());
        dash.put("productsTotal",     productRepository.count());
        dash.put("lowStockCount",     productRepository.findLowStockProducts().size());
        dash.put("ordersByStatus",    ordersByStatus);
        return dash;
    }

    public List<Map<String, Object>> getRevenueTrend(int days) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd");
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date     = LocalDate.now().minusDays(i);
            LocalDateTime from = date.atStartOfDay();
            LocalDateTime to   = from.plusDays(1);

            BigDecimal revenue = orderRepository.sumRevenueBetween(from, to);
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date",    date.format(fmt));
            point.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
            result.add(point);
        }
        return result;
    }

    public List<Map<String, Object>> getTopProducts(int limit) {
        Map<Long, long[]> map = new HashMap<>();   
        Map<Long, String> names = new HashMap<>();

        orderRepository.findAll().forEach(order -> {
            if (order.getStatus() == OrderStatus.CANCELLED) return;
            order.getItems().forEach(item -> {
                Long pid = item.getProduct().getId();
                names.put(pid, item.getProduct().getName());
                map.computeIfAbsent(pid, k -> new long[]{0, 0});
                map.get(pid)[0] += item.getQuantity();
                map.get(pid)[1] += item.getLineTotal().longValue();
            });
        });

        return map.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
            .limit(limit)
            .map(e -> {
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("productId",     e.getKey());
                r.put("productName",   names.get(e.getKey()));
                r.put("totalQuantity", e.getValue()[0]);
                r.put("totalRevenue",  e.getValue()[1]);
                return r;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSalespersonPerformance() {
        Map<Long, Map<String, Object>> map = new LinkedHashMap<>();

        orderRepository.findAll().forEach(order -> {
            Long uid = order.getSalesperson().getId();
            map.computeIfAbsent(uid, k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("salespersonId",   uid);
                m.put("salespersonName", order.getSalesperson().getName());
                m.put("totalOrders",     0L);
                m.put("deliveredOrders", 0L);
                m.put("totalRevenue",    BigDecimal.ZERO);
                return m;
            });

            Map<String, Object> entry = map.get(uid);
            entry.put("totalOrders", ((Long) entry.get("totalOrders")) + 1);

            if (order.getStatus() == OrderStatus.DELIVERED) {
                entry.put("deliveredOrders", ((Long) entry.get("deliveredOrders")) + 1);
                entry.put("totalRevenue",    ((BigDecimal) entry.get("totalRevenue")).add(order.getTotal()));
            }
        });

        map.values().forEach(m -> {
            long total     = (Long) m.get("totalOrders");
            long delivered = (Long) m.get("deliveredOrders");
            m.put("conversionRate", total > 0 ? (delivered * 100.0 / total) : 0.0);
        });

        return new ArrayList<>(map.values());
    }

    public List<Map<String, Object>> getRevenueByCategory() {
        Map<String, BigDecimal> catRevenue = new LinkedHashMap<>();

        orderRepository.findAll().forEach(order -> {
            if (order.getStatus() == OrderStatus.CANCELLED) return;
            order.getItems().forEach(item -> {
                String cat = item.getProduct().getCategory() != null
                    ? item.getProduct().getCategory().getName() : "Uncategorized";
                catRevenue.merge(cat, item.getLineTotal(), BigDecimal::add);
            });
        });

        return catRevenue.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .map(e -> {
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("categoryName",  e.getKey());
                r.put("totalRevenue",  e.getValue());
                return r;
            })
            .collect(Collectors.toList());
    }
}
