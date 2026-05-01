import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// ── Axios Instance ──
const api = axios.create({ baseURL: BASE_URL });

// Attach JWT to every request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Global error handler
api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// ── Auth ──
export const authAPI = {
  login:  (email, password) => axios.post(`${BASE_URL}/auth/login`, { email, password }),
  register: (data)          => axios.post(`${BASE_URL}/auth/register`, data),
  getMe:  (token)           => axios.get(`${BASE_URL}/auth/me`, { headers: { Authorization: `Bearer ${token}` } }),
};

// ── Users ──
export const userAPI = {
  getAll:  ()       => api.get('/users'),
  create:  (data)   => api.post('/users', data),
  update:  (id, d)  => api.put(`/users/${id}`, d),
  delete:  (id)     => api.delete(`/users/${id}`),
  toggle:  (id)     => api.patch(`/users/${id}/toggle`),
};

// ── Categories ──
export const categoryAPI = {
  getAll:  ()       => api.get('/categories'),
  create:  (data)   => api.post('/categories', data),
  update:  (id, d)  => api.put(`/categories/${id}`, d),
  delete:  (id)     => api.delete(`/categories/${id}`),
};

// ── Products ──
export const productAPI = {
  getAll:     (params) => api.get('/products', { params }),
  getById:    (id)     => api.get(`/products/${id}`),
  getLowStock:()       => api.get('/products/low-stock'),
  create:     (data)   => api.post('/products', data),
  update:     (id, d)  => api.put(`/products/${id}`, d),
  delete:     (id)     => api.delete(`/products/${id}`),
  exportCsv:  ()       => api.get('/products/export', { responseType: 'blob' }),
  importCsv:  (file)   => { const fd = new FormData(); fd.append('file', file); return api.post('/products/import', fd); },
};

// ── Customers ──
export const customerAPI = {
  getAll:   (params) => api.get('/customers', { params }),
  getById:  (id)     => api.get(`/customers/${id}`),
  getOrders:(id)     => api.get(`/customers/${id}/orders`),
  create:   (data)   => api.post('/customers', data),
  update:   (id, d)  => api.put(`/customers/${id}`, d),
  delete:   (id)     => api.delete(`/customers/${id}`),
};

// ── Orders ──
export const orderAPI = {
  getAll:      (params) => api.get('/orders', { params }),
  getById:     (id)     => api.get(`/orders/${id}`),
  create:      (data)   => api.post('/orders', data),
  updateStatus:(id, s)  => api.patch(`/orders/${id}/status`, { status: s }),
  delete:      (id)     => api.delete(`/orders/${id}`),
};

// ── Invoices ──
export const invoiceAPI = {
  generate:  (orderId) => api.post(`/invoices/generate/${orderId}`),
  getAll:    ()        => api.get('/invoices'),
  getById:   (id)      => api.get(`/invoices/${id}`),
  download:  (id)      => api.get(`/invoices/${id}/download`, { responseType: 'blob' }),
};

// ── Reports ──
export const reportAPI = {
  getDashboard: ()       => api.get('/reports/dashboard'),
  getRevenue:   (params) => api.get('/reports/revenue', { params }),
  getTopProducts:(limit) => api.get('/reports/top-products', { params: { limit } }),
  getSalesperson:()      => api.get('/reports/salesperson'),
  getByCategory: ()      => api.get('/reports/categories'),
};

// ── Audit Logs ──
export const auditAPI = {
  getAll: (params) => api.get('/audit-logs', { params }),
};

export default api;
