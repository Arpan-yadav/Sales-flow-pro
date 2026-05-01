# Project Report: SalesFlow Pro (Swing Edition)
**100% Java Desktop Management System**

---

## 1. Executive Summary
**SalesFlow Pro (Swing Edition)** is a robust, enterprise-grade desktop application designed for managing sales, inventory, and customer relationships. Unlike the web-based version, this edition is built entirely in **Java** using the **Swing GUI framework**, making it a high-performance, standalone desktop solution. It leverages **Spring Boot** internally for data management (JPA) and security (BCrypt), but eliminates all dependencies on JavaScript, HTML, or external web servers.

---

## 2. Technical Stack
*   **Language**: Java 17+ (Optimized for Java 25 compatibility).
*   **GUI Framework**: Java Swing (Core AWT/Swing libraries).
*   **Backend Framework**: Spring Boot (Non-Web mode) for Dependency Injection and Service management.
*   **Persistence**: Spring Data JPA with Hibernate.
*   **Database**: PostgreSQL 16.
*   **Security**: BCrypt Password Encoding.
*   **Reporting**: iText7 (PDF Invoice Generation) and Apache POI (Excel/CSV Export).

---

## 3. Core Modules & Features
### A. Authentication & Security
*   **Secure Login**: BCrypt-based authentication ensuring passwords are never stored in plain text.
*   **Role Management**: Supports Admin and Salesperson roles.

### B. Dashboard & Analytics
*   **Live Statistics**: Real-time calculation of daily revenue, total orders, and customer counts.
*   **Low-Stock Alerts**: Visual indicators for products that fall below the inventory threshold.

### C. Inventory Management
*   **Product Catalog**: Detailed table view of all products, including prices, SKU, and categories.
*   **Eager Loading**: Optimized database queries to ensure instant data availability in the UI.

### D. Sales & Order Tracking
*   **Order History**: Comprehensive log of all transactions with status tracking (Pending, Delivered, Cancelled).
*   **Customer Directory**: Centralized database of customer contact information and status.

---

## 4. Architectural Design
The project follows a **Modified Layered Architecture** adapted for Desktop use:
1.  **UI Layer**: Swing `JFrames` and `JPanels` handling user interaction.
2.  **Service Layer**: Business logic for calculating taxes, checking stock, and processing orders.
3.  **Repository Layer**: Spring Data JPA interfaces for seamless PostgreSQL communication.
4.  **Model Layer**: Pure Java POJOs (Entities) mapped to database tables.

---

## 5. Deployment & Execution
The application is packaged as a **Fat JAR**, containing all dependencies (Spring, Hibernate, Postgres Driver). It runs as a single process, connecting directly to the PostgreSQL instance defined in `application.properties`.

---

## 6. Conclusion
This 100% Java version of **SalesFlow Pro** demonstrates the power of Java for building reliable, secure, and fast business applications. By removing the complexity of a web browser, it provides a stable and consistent experience for internal business management.
