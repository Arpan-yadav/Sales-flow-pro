-- ╔══════════════════════════════════════════════╗
-- ║  SalesFlow Pro — PostgreSQL Setup Script     ║
-- ║  Run this BEFORE starting the backend        ║
-- ╚══════════════════════════════════════════════╝

-- 1. Create the database
CREATE DATABASE sales_db;

-- 2. Connect to it: \c sales_db

-- 3. (Optional) Verify connection
SELECT current_database();

-- Tables are auto-created by Hibernate (ddl-auto=update).
-- Default admin is seeded on first startup:
--   Email:    admin@sales.com
--   Password: Admin@123
