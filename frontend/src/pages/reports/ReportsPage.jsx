import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { reportAPI } from '../../services/api';
import {
  AreaChart, Area, BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, Legend
} from 'recharts';

export default function ReportsPage() {
  const [days, setDays] = useState(30);

  const { data: revenue  } = useQuery({ queryKey: ['revenue', days],  queryFn: () => reportAPI.getRevenue({ days }).then(r => r.data.data) });
  const { data: topProds } = useQuery({ queryKey: ['top-products-10'], queryFn: () => reportAPI.getTopProducts(10).then(r => r.data.data) });
  const { data: sales    } = useQuery({ queryKey: ['salesperson'],      queryFn: () => reportAPI.getSalesperson().then(r => r.data.data) });
  const { data: cats     } = useQuery({ queryKey: ['categories-report'],queryFn: () => reportAPI.getByCategory().then(r => r.data.data) });

  return (
    <div>
      {/* Revenue Trend */}
      <div className="card" style={{ marginBottom: 20 }}>
        <div className="card-header">
          <div><div className="card-title">Revenue Trend</div><div className="card-subtitle">Daily breakdown</div></div>
          <div style={{ display: 'flex', gap: 8 }}>
            {[7, 30, 90].map(d => (
              <button key={d} onClick={() => setDays(d)}
                className={`badge ${days === d ? 'badge-blue' : 'badge-gray'}`}
                style={{ cursor: 'pointer', padding: '5px 12px' }}>
                {d}d
              </button>
            ))}
          </div>
        </div>
        <ResponsiveContainer width="100%" height={260}>
          <AreaChart data={revenue || []} margin={{ top: 5, right: 10, left: 10, bottom: 0 }}>
            <defs>
              <linearGradient id="revGrad" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#2563EB" stopOpacity={0.15} />
                <stop offset="95%" stopColor="#2563EB" stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
            <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} />
            <YAxis tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} tickFormatter={v => `₹${(v/1000).toFixed(0)}k`} />
            <Tooltip formatter={v => [`₹${Number(v).toLocaleString('en-IN')}`, 'Revenue']} contentStyle={{ borderRadius: 8, border: '1px solid #E2E8F0', fontSize: 12 }} />
            <Area type="monotone" dataKey="revenue" stroke="#2563EB" strokeWidth={2.5} fill="url(#revGrad)" dot={false} />
          </AreaChart>
        </ResponsiveContainer>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 20 }}>
        {/* Top Products */}
        <div className="card">
          <div className="card-title" style={{ marginBottom: 16 }}>Top Products by Units Sold</div>
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={topProds || []} layout="vertical" margin={{ top: 0, right: 20, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" horizontal={false} />
              <XAxis type="number" tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} />
              <YAxis type="category" dataKey="productName" tick={{ fontSize: 11, fill: '#64748B' }} axisLine={false} tickLine={false} width={100} />
              <Tooltip contentStyle={{ borderRadius: 8, border: '1px solid #E2E8F0', fontSize: 12 }} />
              <Bar dataKey="totalQuantity" fill="#2563EB" radius={[0, 4, 4, 0]} name="Units" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Category breakdown */}
        <div className="card">
          <div className="card-title" style={{ marginBottom: 16 }}>Revenue by Category</div>
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={cats || []} margin={{ top: 0, right: 10, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
              <XAxis dataKey="categoryName" tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: '#94A3B8' }} axisLine={false} tickLine={false} tickFormatter={v => `₹${(v/1000).toFixed(0)}k`} />
              <Tooltip contentStyle={{ borderRadius: 8, border: '1px solid #E2E8F0', fontSize: 12 }} formatter={v => [`₹${Number(v).toLocaleString('en-IN')}`, 'Revenue']} />
              <Bar dataKey="totalRevenue" fill="#059669" radius={[4, 4, 0, 0]} name="Revenue" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Salesperson Performance */}
      <div className="card">
        <div className="card-title" style={{ marginBottom: 16 }}>Salesperson Performance</div>
        <div className="table-wrap" style={{ boxShadow: 'none', border: 'none' }}>
          <table className="data-table">
            <thead><tr><th>Salesperson</th><th>Total Orders</th><th>Delivered</th><th>Revenue Generated</th><th>Conversion Rate</th></tr></thead>
            <tbody>
              {(sales || []).map(s => (
                <tr key={s.salespersonId}>
                  <td style={{ fontWeight: 600 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <div className="avatar" style={{ width: 28, height: 28, fontSize: '0.7rem' }}>
                        {s.salespersonName?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()}
                      </div>
                      {s.salespersonName}
                    </div>
                  </td>
                  <td>{s.totalOrders}</td>
                  <td>{s.deliveredOrders}</td>
                  <td style={{ fontWeight: 700 }}>₹{Number(s.totalRevenue).toLocaleString('en-IN')}</td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <div style={{ flex: 1, height: 6, background: 'var(--color-bg-alt)', borderRadius: 3 }}>
                        <div style={{ width: `${s.conversionRate}%`, height: '100%', background: 'var(--color-primary)', borderRadius: 3 }} />
                      </div>
                      <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--color-primary)', width: 40 }}>{s.conversionRate?.toFixed(1)}%</span>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
