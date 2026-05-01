import { useQuery } from '@tanstack/react-query';
import { reportAPI } from '../../services/api';
import { Link } from 'react-router-dom';
import {
  AreaChart, Area, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend
} from 'recharts';
import { DollarSign, ShoppingCart, Package, Users, TrendingUp, AlertTriangle } from 'lucide-react';

const STATUS_COLORS = {
  PENDING: '#D97706', CONFIRMED: '#2563EB', SHIPPED: '#0891B2',
  DELIVERED: '#059669', CANCELLED: '#DC2626',
};

export default function DashboardPage() {
  const { data: dash, isLoading } = useQuery({
    queryKey: ['dashboard'],
    queryFn: () => reportAPI.getDashboard().then(r => r.data.data),
  });

  const { data: revenue } = useQuery({
    queryKey: ['revenue-chart'],
    queryFn: () => reportAPI.getRevenue({ days: 30 }).then(r => r.data.data),
  });

  const { data: topProducts } = useQuery({
    queryKey: ['top-products'],
    queryFn: () => reportAPI.getTopProducts(5).then(r => r.data.data),
  });

  if (isLoading) return <DashboardSkeleton />;

  const stats = dash || {};
  const orderStatusData = Object.entries(stats.ordersByStatus || {}).map(([k, v]) => ({ name: k, value: v }));
  const PIE_COLORS = Object.values(STATUS_COLORS);

  return (
    <div>
      {/* KPI Grid */}
      <div className="stat-grid">
        <StatCard label="Today's Revenue"   value={fmt(stats.revenueToday)}   icon={DollarSign} color="blue"   change={stats.revenueChangePct} />
        <StatCard label="Monthly Revenue"   value={fmt(stats.revenueMonth)}   icon={TrendingUp}  color="green"  change={null} />
        <StatCard label="Total Orders"      value={stats.ordersTotal}          icon={ShoppingCart} color="purple" change={null} />
        <StatCard label="Active Customers"  value={stats.customersTotal}       icon={Users}       color="cyan"  change={null} />
        <StatCard label="Products"          value={stats.productsTotal}        icon={Package}     color="amber" change={null} />
        <StatCard label="Low Stock Alerts"  value={stats.lowStockCount}        icon={AlertTriangle} color="red"  change={null} />
      </div>

      {/* Charts Row 1 */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 20, marginBottom: 20 }}>
        {/* Revenue area chart */}
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Revenue Trend</div>
              <div className="card-subtitle">Last 30 days</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <AreaChart data={revenue || []} margin={{ top: 5, right: 10, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="revenueGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%"  stopColor="#2563EB" stopOpacity={0.15} />
                  <stop offset="95%" stopColor="#2563EB" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
              <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} tickFormatter={v => `₹${(v/1000).toFixed(0)}k`} />
              <Tooltip formatter={v => [`₹${Number(v).toLocaleString('en-IN')}`, 'Revenue']} contentStyle={{ borderRadius: 8, border: '1px solid #E2E8F0', fontSize: 12 }} />
              <Area type="monotone" dataKey="revenue" stroke="#2563EB" strokeWidth={2} fill="url(#revenueGrad)" dot={false} activeDot={{ r: 4 }} />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Order status pie */}
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Order Status</div>
              <div className="card-subtitle">Current distribution</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <PieChart>
              <Pie data={orderStatusData} cx="50%" cy="50%" innerRadius={60} outerRadius={90}
                dataKey="value" paddingAngle={3}>
                {orderStatusData.map((_, i) => (
                  <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ borderRadius: 8, border: '1px solid #E2E8F0', fontSize: 12 }} />
              <Legend iconType="circle" iconSize={10} formatter={(v) => <span style={{ fontSize: 11, color: '#64748B' }}>{v}</span>} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts Row 2 */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
        {/* Top products bar chart */}
        <div className="card">
          <div className="card-header">
            <div className="card-title">Top Products</div>
            <Link to="/reports" className="btn btn-ghost btn-sm">View all</Link>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={topProducts || []} layout="vertical" margin={{ top: 0, right: 10, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" horizontal={false} />
              <XAxis type="number" tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} />
              <YAxis type="category" dataKey="productName" tick={{ fontSize: 11, fill: '#64748B' }} axisLine={false} tickLine={false} width={90} />
              <Tooltip contentStyle={{ borderRadius: 8, border: '1px solid #E2E8F0', fontSize: 12 }} />
              <Bar dataKey="totalQuantity" fill="#2563EB" radius={[0, 4, 4, 0]} name="Units Sold" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Quick actions */}
        <div className="card">
          <div className="card-title" style={{ marginBottom: 16 }}>Quick Actions</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <Link to="/orders/new" className="btn btn-primary">+ New Order</Link>
            <Link to="/customers" className="btn btn-secondary">+ Add Customer</Link>
            <Link to="/products" className="btn btn-secondary">+ Add Product</Link>
            <Link to="/reports" className="btn btn-secondary">View Reports</Link>
          </div>
        </div>
      </div>
    </div>
  );
}

const fmt = val => val != null
  ? `₹${Number(val).toLocaleString('en-IN', { minimumFractionDigits: 0 })}`
  : '₹0';

function StatCard({ label, value, icon: Icon, color, change }) {
  return (
    <div className="stat-card">
      <div className={`stat-icon-wrap ${color}`}><Icon size={22} /></div>
      <div className="stat-label">{label}</div>
      <div className="stat-value">{value ?? 0}</div>
      {change != null && (
        <div className={`stat-change ${change >= 0 ? 'up' : 'down'}`}>
          {change >= 0 ? '↑' : '↓'} {Math.abs(change).toFixed(1)}% vs yesterday
        </div>
      )}
    </div>
  );
}

function DashboardSkeleton() {
  return (
    <div>
      <div className="stat-grid">
        {[...Array(6)].map((_, i) => (
          <div key={i} style={{ height: 130, borderRadius: 'var(--radius-lg)' }} className="skeleton" />
        ))}
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 20, marginBottom: 20 }}>
        <div style={{ height: 300, borderRadius: 'var(--radius-lg)' }} className="skeleton" />
        <div style={{ height: 300, borderRadius: 'var(--radius-lg)' }} className="skeleton" />
      </div>
    </div>
  );
}
