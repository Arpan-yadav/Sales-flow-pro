# 🛒 SalesFlow Pro — Java Sales Management System

> Full-stack capstone: **Spring Boot 3** + **React 19** + **PostgreSQL**

---

## 🚀 Quick Start

### Prerequisites
- Java JDK 17+
- Maven 3.9+
- PostgreSQL 15+
- Node.js 20+

---

### 1. Database Setup
```sql
-- In psql or pgAdmin:
CREATE DATABASE sales_db;
```

### 2. Backend — Configure & Run
```bash
# Edit backend/src/main/resources/application.properties
# Set your PostgreSQL password:
spring.datasource.password=YOUR_PASSWORD

# Run from backend/ directory:
cd backend
mvn spring-boot:run
```
Backend starts at: `http://localhost:8080`

On first startup:
- ✅ Tables auto-created by Hibernate
- ✅ Default admin seeded: `admin@sales.com` / `Admin@123`
- ✅ **50 orders**, **25 products**, **20 customers** auto-loaded (Superstore dataset)

### 3. Frontend — Install & Run
```bash
cd frontend
npm install
npm run dev
```
Frontend starts at: `http://localhost:5173`

---

## 👤 Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@sales.com | Admin@123 |
| Sales Manager | priya@sales.com | Sales@123 |
| Salesperson | anjali@sales.com | Sales@123 |

---

## 📦 Features

| Feature | Status |
|---------|--------|
| JWT Auth + Role-based access | ✅ |
| Product & Inventory management | ✅ |
| Customer CRM | ✅ |
| Order creation & status tracking | ✅ |
| PDF Invoice generation (iText 8) | ✅ |
| Dashboard with Recharts | ✅ |
| Sales Reports & Analytics | ✅ |
| CSV Export/Import | ✅ |
| Audit Logs | ✅ |
| Superstore dataset (50 orders) | ✅ |

---

## 🗂️ API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login |
| GET | `/api/products` | List products |
| POST | `/api/orders` | Create order |
| PATCH | `/api/orders/{id}/status` | Update status |
| POST | `/api/invoices/generate/{orderId}` | Generate PDF |
| GET | `/api/invoices/{id}/download` | Download PDF |
| GET | `/api/reports/dashboard` | KPI summary |
| GET | `/api/reports/revenue?days=30` | Revenue trend |
