package com.sales.seeder;

import com.sales.model.*;
import com.sales.model.enums.OrderStatus;
import com.sales.model.enums.Role;
import com.sales.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
@Order(2)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final CategoryRepository category;
    private final ProductRepository  product;
    private final CustomerRepository customer;
    private final UserRepository     user;
    private final OrderRepository    order;
    private final PasswordEncoder    encoder;

    public DataSeeder(CategoryRepository category, ProductRepository product, CustomerRepository customer, UserRepository user, OrderRepository order, PasswordEncoder encoder) {
        this.category = category;
        this.product = product;
        this.customer = customer;
        this.user = user;
        this.order = order;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (product.count() > 0) {
            log.info("✅ Data already seeded — skipping.");
            return;
        }
        log.info("🌱 Seeding Superstore-style dataset...");

        List<Category> cats    = seedCategories();
        List<Product>  prods   = seedProducts(cats);
        List<User>     users   = seedUsers();
        List<Customer> custs   = seedCustomers();
        seedOrders(prods, users, custs);

        log.info("✅ Seeded {} categories, {} products, {} customers, {} users.",
            cats.size(), prods.size(), custs.size(), users.size());
    }

    private List<Category> seedCategories() {
        return List.of(
            cat("Technology",      "Computers, phones, electronics & accessories"),
            cat("Furniture",       "Chairs, tables, bookcases & office furniture"),
            cat("Office Supplies", "Binders, paper, labels, envelopes & art supplies"),
            cat("Clothing",        "Corporate apparel, workwear & accessories"),
            cat("Sports & Outdoors","Sports equipment, outdoor gear & fitness")
        );
    }

    private List<Product> seedProducts(List<Category> cats) {
        Category tech    = cats.get(0);
        Category furn    = cats.get(1);
        Category office  = cats.get(2);
        Category cloth   = cats.get(3);
        Category sports  = cats.get(4);

        List<Product> products = new ArrayList<>();
        products.add(prod("Apple MacBook Pro 14\"",       "TEC-001", tech,   149999, 20, 5));
        products.add(prod("Dell Inspiron 15 Laptop",       "TEC-002", tech,    54999, 35, 8));
        products.add(prod("Samsung Galaxy S24 Ultra",      "TEC-003", tech,   119999, 25, 5));
        products.add(prod("Apple iPhone 15 Pro",           "TEC-004", tech,   129999, 18, 5));
        products.add(prod("Sony WH-1000XM5 Headphones",   "TEC-005", tech,    29999, 40, 10));
        products.add(prod("Logitech MX Master 3 Mouse",   "TEC-006", tech,     7999, 60, 15));
        products.add(prod("HP LaserJet Pro Printer",       "TEC-007", tech,    24999, 12, 4));
        products.add(prod("iPad Pro 12.9\" (2024)",        "TEC-008", tech,   109999, 15, 5));
        products.add(prod("Cisco IP Phone 8845",           "TEC-009", tech,    18999, 8,  3));
        products.add(prod("Kingston 32GB DDR5 RAM",        "TEC-010", tech,     5499, 80, 20));
        products.add(prod("Ergonomic Office Chair (Mesh)", "FUR-001", furn,   18999, 15, 4));
        products.add(prod("Standing Desk (Height Adjust)", "FUR-002", furn,   29999, 10, 3));
        products.add(prod("3-Shelf Wooden Bookcase",       "FUR-003", furn,    8499, 22, 5));
        products.add(prod("Executive Conference Table",    "FUR-004", furn,   54999, 5,  2));
        products.add(prod("Filing Cabinet (4-Drawer)",     "FUR-005", furn,    9999, 18, 5));
        products.add(prod("Avery Heavy-Duty Binder",       "OFF-001", office,   499, 200, 30));
        products.add(prod("Staples Copy Paper (500 sht)",  "OFF-002", office,   349, 500, 50));
        products.add(prod("Post-it Notes (12-Pack)",       "OFF-003", office,   299, 350, 50));
        products.add(prod("Sharpie Marker Set (20pc)",     "OFF-004", office,   599, 150, 30));
        products.add(prod("Brother Label Maker",           "OFF-005", office,  2499, 45,  10));
        products.add(prod("Corporate Polo T-Shirt",        "CLO-001", cloth,  1299, 100, 20));
        products.add(prod("Business Formal Trousers",      "CLO-002", cloth,  2499, 60,  15));
        products.add(prod("Adjustable Dumbbell Set (20kg)","SPO-001", sports, 4999, 30,  8));
        products.add(prod("Yoga Mat (Premium Cork)",       "SPO-002", sports,  1499, 70,  15));
        products.add(prod("Resistance Bands Kit",          "SPO-003", sports,   799, 90,  20));

        return products;
    }

    private List<User> seedUsers() {
        List<User> users = new ArrayList<>();
        users.add(usr("Priya Sharma",      "priya@sales.com",   "Sales@123", Role.SALES_MANAGER, "9876543210"));
        users.add(usr("Rahul Verma",       "rahul@sales.com",   "Sales@123", Role.SALES_MANAGER, "9876543211"));
        users.add(usr("Anjali Mehta",      "anjali@sales.com",  "Sales@123", Role.SALESPERSON,   "9876543212"));
        users.add(usr("Vikram Singh",      "vikram@sales.com",  "Sales@123", Role.SALESPERSON,   "9876543213"));
        users.add(usr("Sneha Kapoor",      "sneha@sales.com",  "Sales@123", Role.SALESPERSON,   "9876543214"));
        users.add(usr("Arjun Nair",        "arjun@sales.com",  "Sales@123", Role.SALESPERSON,   "9876543215"));
        users.add(usr("Pooja Reddy",       "pooja@sales.com",  "Sales@123", Role.SALESPERSON,   "9876543216"));
        return users;
    }

    private List<Customer> seedCustomers() {
        return List.of(
            cust("Infosys Limited",        "procurement@infosys.com",   "08022874374", "Electronics City, Bangalore",     "Bangalore",  "India"),
            cust("TCS Global Solutions",   "orders@tcs.com",            "02261081000", "TCS House, Mumbai",               "Mumbai",     "India"),
            cust("Wipro Technologies",     "supplies@wipro.com",        "08028440011", "Doddakannelli, Bangalore",         "Bangalore",  "India"),
            cust("HDFC Bank Ltd",          "admin@hdfcbank.com",        "02261606160", "HDFC Bank House, Mumbai",          "Mumbai",     "India"),
            cust("Reliance Industries",    "purchase@ril.com",          "02222786000", "Maker Chambers, Mumbai",           "Mumbai",     "India"),
            cust("Aditya Birla Group",     "supplies@adityabirla.com",  "02266527000", "One Indiabulls, Mumbai",           "Mumbai",     "India"),
            cust("Mahindra & Mahindra",    "procurement@mahindra.com",  "02224901441", "Mahindra Towers, Mumbai",          "Mumbai",     "India"),
            cust("Tata Consultancy",       "info@tataconsultancy.com",  "02222626000", "Bombay House, Mumbai",             "Mumbai",     "India"),
            cust("HCL Technologies",       "buy@hcl.com",               "01204260000", "HCL House, Noida",                "Noida",      "India"),
            cust("Bajaj Finserv",          "office@bajajfinserv.com",   "02066073333", "Viman Nagar, Pune",               "Pune",       "India"),
            cust("Sun Pharma",             "admin@sunpharma.com",       "02240494000", "Andheri East, Mumbai",            "Mumbai",     "India"),
            cust("Bharat Petroleum",       "purchase@bpcl.in",          "02222715000", "Ballard Estate, Mumbai",          "Mumbai",     "India"),
            cust("Asian Paints",           "orders@asianpaints.com",    "02267181000", "Andheri East, Mumbai",            "Mumbai",     "India"),
            cust("Maruti Suzuki India",    "supplies@maruti.co.in",     "01146781000", "Nelson Mandela Road, Delhi",      "New Delhi",  "India"),
            cust("Flipkart Private Ltd",   "b2b@flipkart.com",          "08030455000", "Embassy Tech, Bangalore",         "Bangalore",  "India"),
            cust("Zomato Ltd",             "corporate@zomato.com",      "01130802484", "DLF Cyber City, Gurugram",        "Gurugram",   "India"),
            cust("Paytm (One97 Comm.)",    "enterprise@paytm.com",      "01204560000", "B-121 Sector 5, Noida",           "Noida",      "India"),
            cust("Ola Electric",           "corp@olaelectric.com",      "08067500000", "ANZ Garden City, Bangalore",      "Bangalore",  "India"),
            cust("Byju's Ltd",             "supplies@byjus.com",        "08040391200", "Think School, Bangalore",         "Bangalore",  "India"),
            cust("Nykaa E-Retail",         "b2b@nykaa.com",             "02240484055", "Andheri West, Mumbai",            "Mumbai",     "India")
        );
    }

    private void seedOrders(List<Product> prods, List<User> users, List<Customer> custs) {
        Object[][] orderDefs = {
            {0, 0, 5.0, 18.0, "DELIVERED", 180, new int[][]{{0,1},{5,2}}},
            {1, 2, 0.0, 18.0, "DELIVERED", 175, new int[][]{{1,2},{9,4},{15,10}}},
            {2, 3, 10.0,18.0, "DELIVERED", 170, new int[][]{{10,5},{12,3}}},
            {3, 4, 0.0, 18.0, "DELIVERED", 165, new int[][]{{6,1},{16,20}}},
            {4, 5, 5.0, 18.0, "DELIVERED", 160, new int[][]{{2,3},{7,2}}},
            {5, 2, 0.0, 18.0, "DELIVERED", 155, new int[][]{{3,2},{5,5},{17,30}}},
            {6, 3, 10.0,18.0, "DELIVERED", 150, new int[][]{{11,2},{20,10}}},
            {7, 4, 0.0, 18.0, "DELIVERED", 145, new int[][]{{0,1},{8,1}}},
            {8, 5, 0.0, 18.0, "DELIVERED", 140, new int[][]{{4,4},{18,5}}},
            {9, 0, 5.0, 18.0, "DELIVERED", 135, new int[][]{{1,1},{16,15}}},
            {10,2, 0.0, 18.0, "DELIVERED", 130, new int[][]{{13,1},{15,5}}},
            {11,3, 10.0,18.0, "DELIVERED", 125, new int[][]{{10,3},{14,2}}},
            {12,4, 0.0, 18.0, "DELIVERED", 120, new int[][]{{2,2},{6,1},{19,2}}},
            {13,5, 5.0, 18.0, "DELIVERED", 115, new int[][]{{0,2},{9,8}}},
            {14,2, 0.0, 18.0, "DELIVERED", 110, new int[][]{{7,1},{17,25}}},
            {15,3, 0.0, 18.0, "DELIVERED", 105, new int[][]{{3,3},{22,2}}},
            {16,4, 5.0, 18.0, "DELIVERED", 100, new int[][]{{21,5},{23,8}}},
            {17,5, 0.0, 18.0, "DELIVERED", 95,  new int[][]{{1,1},{5,3},{16,20}}},
            {18,0, 10.0,18.0, "DELIVERED", 90,  new int[][]{{10,4},{24,10}}},
            {19,2, 0.0, 18.0, "DELIVERED", 85,  new int[][]{{0,1},{4,2}}},
            {0, 3, 5.0, 18.0, "DELIVERED", 80,  new int[][]{{6,1},{12,2}}},
            {1, 4, 0.0, 18.0, "DELIVERED", 75,  new int[][]{{2,1},{11,3},{23,5}}},
            {2, 5, 0.0, 18.0, "DELIVERED", 70,  new int[][]{{9,6},{15,15}}},
            {3, 2, 5.0, 18.0, "SHIPPED",   65,  new int[][]{{1,2},{8,1}}},
            {4, 3, 0.0, 18.0, "SHIPPED",   60,  new int[][]{{3,1},{17,20}}},
            {5, 4, 10.0,18.0, "SHIPPED",   55,  new int[][]{{0,1},{7,1}}},
            {6, 5, 0.0, 18.0, "CONFIRMED", 50,  new int[][]{{10,2},{21,8}}},
            {7, 0, 5.0, 18.0, "CONFIRMED", 45,  new int[][]{{4,3},{16,10}}},
            {8, 2, 0.0, 18.0, "CONFIRMED", 40,  new int[][]{{2,2},{5,4}}},
            {9, 3, 0.0, 18.0, "CONFIRMED", 35,  new int[][]{{6,1},{12,1},{22,3}}},
            {10,4, 5.0, 18.0, "PENDING",   30,  new int[][]{{1,1},{9,5}}},
            {11,5, 0.0, 18.0, "PENDING",   28,  new int[][]{{13,1},{24,15}}},
            {12,2, 10.0,18.0, "PENDING",   25,  new int[][]{{0,1},{4,2}}},
            {13,3, 0.0, 18.0, "PENDING",   22,  new int[][]{{11,2},{20,12}}},
            {14,4, 5.0, 18.0, "PENDING",   20,  new int[][]{{3,1},{15,8}}},
            {15,5, 0.0, 18.0, "PENDING",   18,  new int[][]{{8,1},{17,30}}},
            {16,0, 0.0, 18.0, "PENDING",   15,  new int[][]{{10,3},{23,6}}},
            {17,2, 5.0, 18.0, "CANCELLED", 14,  new int[][]{{0,1}}},
            {18,3, 0.0, 18.0, "CANCELLED", 12,  new int[][]{{1,2},{6,1}}},
            {19,4, 10.0,18.0, "PENDING",   10,  new int[][]{{2,1},{16,20}}},
            {0, 5, 0.0, 18.0, "PENDING",   8,   new int[][]{{7,1},{9,4}}},
            {1, 2, 5.0, 18.0, "PENDING",   7,   new int[][]{{4,2},{22,5}}},
            {2, 3, 0.0, 18.0, "CONFIRMED", 6,   new int[][]{{1,1},{5,3}}},
            {3, 4, 0.0, 18.0, "PENDING",   5,   new int[][]{{0,1},{11,2}}},
            {4, 5, 5.0, 18.0, "PENDING",   4,   new int[][]{{3,1},{15,10}}},
            {5, 0, 0.0, 18.0, "PENDING",   3,   new int[][]{{6,2},{16,8}}},
            {6, 2, 10.0,18.0, "PENDING",   2,   new int[][]{{10,1},{23,4}}},
            {7, 3, 0.0, 18.0, "PENDING",   1,   new int[][]{{4,1},{9,3}}},
            {8, 4, 5.0, 18.0, "PENDING",   0,   new int[][]{{2,1},{17,15}}},
            {9, 5, 0.0, 18.0, "PENDING",   0,   new int[][]{{13,1},{24,8}}},
        };

        for (Object[] def : orderDefs) {
            int  custIdx    = (int) def[0];
            int  salesIdx   = (int) def[1];
            double discPct  = (double) def[2];
            double taxPct   = (double) def[3];
            String statusStr= (String) def[4];
            Customer c   = custs.get(custIdx % custs.size());
            User     sp  = users.get(salesIdx % users.size());

            com.sales.model.Order ord = new com.sales.model.Order();
            ord.setCustomer(c);
            ord.setSalesperson(sp);
            ord.setStatus(OrderStatus.valueOf(statusStr));
            ord.setDiscountPercent(BigDecimal.valueOf(discPct));
            ord.setTaxPercent(BigDecimal.valueOf(taxPct));

            List<OrderItem> items = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;
            for (int[] itDef : (int[][]) def[6]) {
                Product p = prods.get(itDef[0] % prods.size());
                int qty  = itDef[1];
                BigDecimal unitPrice = p.getPrice();
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                subtotal = subtotal.add(lineTotal);
                OrderItem oi = new OrderItem();
                oi.setOrder(ord);
                oi.setProduct(p);
                oi.setQuantity(qty);
                oi.setUnitPrice(unitPrice);
                oi.setLineTotal(lineTotal);
                items.add(oi);
            }
            ord.setItems(items);
            BigDecimal discAmt = subtotal.multiply(BigDecimal.valueOf(discPct)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal afterDisc = subtotal.subtract(discAmt);
            BigDecimal taxAmt = afterDisc.multiply(BigDecimal.valueOf(taxPct)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            ord.setSubtotal(subtotal);
            ord.setDiscountAmount(discAmt);
            ord.setTaxAmount(taxAmt);
            ord.setTotal(afterDisc.add(taxAmt));
            order.save(ord);
        }
    }

    private Category cat(String name, String desc) {
        return category.findByName(name).orElseGet(() -> {
            Category c = new Category();
            c.setName(name); c.setDescription(desc);
            return category.save(c);
        });
    }

    private Product prod(String name, String sku, Category cat, long priceRs, int stock, int lowThreshold) {
        return product.findBySku(sku).orElseGet(() -> {
            Product p = new Product();
            p.setName(name); p.setSku(sku); p.setCategory(cat);
            p.setPrice(BigDecimal.valueOf(priceRs));
            p.setStockQuantity(stock); p.setLowStockThreshold(lowThreshold);
            p.setActive(true);
            return product.save(p);
        });
    }

    private User usr(String name, String email, String pass, Role role, String phone) {
        return user.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setName(name); u.setEmail(email);
            u.setPassword(encoder.encode(pass));
            u.setRole(role); u.setPhone(phone); u.setActive(true);
            return user.save(u);
        });
    }

    private Customer cust(String name, String email, String phone, String address, String city, String country) {
        return customer.findByEmail(email).orElseGet(() -> {
            Customer c = new Customer();
            c.setName(name); c.setEmail(email); c.setPhone(phone);
            c.setAddress(address); c.setCity(city); c.setCountry(country);
            return customer.save(c);
        });
    }
}
