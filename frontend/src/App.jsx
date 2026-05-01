import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import './index.css';

// Layout
import AppLayout from './components/layout/AppLayout';

// Auth pages
import LoginPage from './pages/auth/LoginPage';

// Dashboard
import DashboardPage from './pages/dashboard/DashboardPage';

// Products
import ProductsPage from './pages/products/ProductsPage';

// Customers
import CustomersPage from './pages/customers/CustomersPage';
import CustomerDetailPage from './pages/customers/CustomerDetailPage';

// Orders
import OrdersPage from './pages/orders/OrdersPage';
import NewOrderPage from './pages/orders/NewOrderPage';
import OrderDetailPage from './pages/orders/OrderDetailPage';

// Invoices
import InvoicesPage from './pages/invoices/InvoicesPage';

// Reports
import ReportsPage from './pages/reports/ReportsPage';

// Admin
import UsersPage from './pages/admin/UsersPage';
import AuditLogsPage from './pages/admin/AuditLogsPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: 1, staleTime: 30_000 },
  },
});

const PrivateRoute = ({ children, adminOnly = false, managerOnly = false }) => {
  const { user, loading, isAdmin, isManager } = useAuth();
  if (loading) return <div style={{display:'flex',alignItems:'center',justifyContent:'center',height:'100vh'}}>
    <div className="spin" style={{width:32,height:32,border:'3px solid #E2E8F0',borderTopColor:'#2563EB',borderRadius:'50%'}}/>
  </div>;
  if (!user) return <Navigate to="/login" replace />;
  if (adminOnly && !isAdmin()) return <Navigate to="/" replace />;
  if (managerOnly && !isManager()) return <Navigate to="/" replace />;
  return children;
};

const AppRoutes = () => {
  const { user } = useAuth();
  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage />} />

      <Route element={<PrivateRoute><AppLayout /></PrivateRoute>}>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/customers" element={<CustomersPage />} />
        <Route path="/customers/:id" element={<CustomerDetailPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/orders/new" element={<NewOrderPage />} />
        <Route path="/orders/:id" element={<OrderDetailPage />} />
        <Route path="/invoices" element={<InvoicesPage />} />
        <Route path="/reports" element={<PrivateRoute managerOnly><ReportsPage /></PrivateRoute>} />
        <Route path="/users" element={<PrivateRoute adminOnly><UsersPage /></PrivateRoute>} />
        <Route path="/audit-logs" element={<PrivateRoute adminOnly><AuditLogsPage /></PrivateRoute>} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <AppRoutes />
          <Toaster
            position="top-right"
            toastOptions={{
              style: { background:'#fff', color:'#0F172A', border:'1px solid #E2E8F0', boxShadow:'0 4px 16px rgba(15,23,42,0.1)', fontFamily:'Inter,sans-serif', fontSize:'0.875rem' },
              success: { iconTheme: { primary:'#059669', secondary:'#fff' } },
              error:   { iconTheme: { primary:'#DC2626', secondary:'#fff' } },
            }}
          />
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  );
}
