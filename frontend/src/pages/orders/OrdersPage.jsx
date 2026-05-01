import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { orderAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Eye, ChevronDown } from 'lucide-react';

const STATUS_BADGE = { PENDING:'badge-amber', CONFIRMED:'badge-blue', SHIPPED:'badge-cyan', DELIVERED:'badge-green', CANCELLED:'badge-red' };
const STATUSES = ['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED'];

export default function OrdersPage() {
  const [statusFilter, setStatusFilter] = useState('');
  const qc = useQueryClient();

  const { data: orders = [], isLoading } = useQuery({
    queryKey: ['orders', statusFilter],
    queryFn: () => orderAPI.getAll({ status: statusFilter || undefined }).then(r => r.data.data),
  });

  const updateStatus = useMutation({
    mutationFn: ({ id, status }) => orderAPI.updateStatus(id, status),
    onSuccess: () => { qc.invalidateQueries(['orders']); toast.success('Order status updated'); },
    onError:   () => toast.error('Failed to update'),
  });

  return (
    <div>
      <div className="toolbar">
        <div style={{ display: 'flex', gap: 8 }}>
          {['', ...STATUSES].map(s => (
            <button
              key={s}
              onClick={() => setStatusFilter(s)}
              className={`badge ${s ? STATUS_BADGE[s] : 'badge-gray'}`}
              style={{ cursor: 'pointer', padding: '6px 14px', border: statusFilter === s ? '2px solid currentColor' : '2px solid transparent' }}
            >
              {s || 'All'}
            </button>
          ))}
        </div>
        <div style={{ flex: 1 }} />
        <Link to="/orders/new" className="btn btn-primary"><Plus size={16} /> New Order</Link>
      </div>

      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr><th>Order #</th><th>Customer</th><th>Salesperson</th><th>Items</th><th>Total</th><th>Status</th><th>Date</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {isLoading ? (
              [...Array(4)].map((_, i) => <tr key={i}><td colSpan={8}><div className="skeleton" style={{ height: 20 }} /></td></tr>)
            ) : orders.length === 0 ? (
              <tr><td colSpan={8}><div className="empty-state"><h3>No orders found</h3></div></td></tr>
            ) : orders.map(o => (
              <tr key={o.id}>
                <td style={{ fontWeight: 700, color: 'var(--color-primary)' }}>#{o.id}</td>
                <td style={{ fontWeight: 600 }}>{o.customerName}</td>
                <td style={{ color: 'var(--color-muted)' }}>{o.salespersonName}</td>
                <td>{o.itemCount}</td>
                <td style={{ fontWeight: 700 }}>₹{Number(o.total).toLocaleString('en-IN')}</td>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                    <span className={`badge ${STATUS_BADGE[o.status]}`}>{o.status}</span>
                    {o.status !== 'DELIVERED' && o.status !== 'CANCELLED' && (
                      <div style={{ position: 'relative' }}>
                        <select
                          style={{ fontSize: '0.7rem', border: '1px solid var(--color-border)', borderRadius: 4, padding: '2px 4px', cursor: 'pointer', background: 'white' }}
                          onChange={e => e.target.value && updateStatus.mutate({ id: o.id, status: e.target.value })}
                          value=""
                        >
                          <option value="">→</option>
                          {STATUSES.filter(s => s !== o.status).map(s => <option key={s} value={s}>{s}</option>)}
                        </select>
                      </div>
                    )}
                  </div>
                </td>
                <td style={{ fontSize: '0.8rem', color: 'var(--color-muted)' }}>
                  {new Date(o.createdAt).toLocaleDateString('en-IN')}
                </td>
                <td>
                  <Link to={`/orders/${o.id}`} className="btn btn-ghost btn-icon btn-sm"><Eye size={15} /></Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
