import { useQuery } from '@tanstack/react-query';
import { useParams, Link } from 'react-router-dom';
import { customerAPI } from '../../services/api';
import { ArrowLeft, Mail, Phone, MapPin } from 'lucide-react';

const STATUS_BADGE = { PENDING:'badge-amber', CONFIRMED:'badge-blue', SHIPPED:'badge-cyan', DELIVERED:'badge-green', CANCELLED:'badge-red' };

export default function CustomerDetailPage() {
  const { id } = useParams();

  const { data: customer, isLoading: loadingC } = useQuery({
    queryKey: ['customer', id],
    queryFn: () => customerAPI.getById(id).then(r => r.data.data),
  });

  const { data: orders = [], isLoading: loadingO } = useQuery({
    queryKey: ['customer-orders', id],
    queryFn: () => customerAPI.getOrders(id).then(r => r.data.data),
  });

  if (loadingC) return <div className="skeleton" style={{ height: 200, borderRadius: 'var(--radius-lg)' }} />;

  const totalSpent = orders.filter(o => o.status === 'DELIVERED').reduce((s, o) => s + Number(o.total), 0);

  return (
    <div>
      <Link to="/customers" className="btn btn-ghost btn-sm" style={{ marginBottom: 20 }}>
        <ArrowLeft size={15} /> Back to Customers
      </Link>

      <div style={{ display: 'grid', gridTemplateColumns: '320px 1fr', gap: 20, alignItems: 'start' }}>
        {/* Profile Card */}
        <div className="card">
          <div style={{ textAlign: 'center', marginBottom: 20 }}>
            <div className="avatar" style={{ width: 64, height: 64, fontSize: '1.4rem', margin: '0 auto 12px' }}>
              {customer?.name?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()}
            </div>
            <h2 style={{ fontSize: '1.2rem' }}>{customer?.name}</h2>
          </div>
          <div className="divider" />
          {[
            { icon: Mail, label: customer?.email || 'No email' },
            { icon: Phone, label: customer?.phone || 'No phone' },
            { icon: MapPin, label: [customer?.city, customer?.country].filter(Boolean).join(', ') || 'No location' },
          ].map(({ icon: Icon, label }) => (
            <div key={label} style={{ display: 'flex', gap: 10, marginBottom: 10, alignItems: 'center', fontSize: '0.875rem', color: 'var(--color-text-2)' }}>
              <Icon size={15} color="var(--color-muted)" /> {label}
            </div>
          ))}
          <div className="divider" />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <div style={{ textAlign: 'center', padding: '12px', background: 'var(--color-bg-alt)', borderRadius: 'var(--radius-md)' }}>
              <div style={{ fontSize: '1.4rem', fontWeight: 800 }}>{orders.length}</div>
              <div style={{ fontSize: '0.72rem', color: 'var(--color-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>Orders</div>
            </div>
            <div style={{ textAlign: 'center', padding: '12px', background: 'var(--color-bg-alt)', borderRadius: 'var(--radius-md)' }}>
              <div style={{ fontSize: '1.1rem', fontWeight: 800 }}>₹{(totalSpent / 1000).toFixed(1)}k</div>
              <div style={{ fontSize: '0.72rem', color: 'var(--color-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>Spent</div>
            </div>
          </div>
        </div>

        {/* Order History */}
        <div className="card">
          <div className="card-header">
            <div className="card-title">Order History</div>
            <Link to="/orders/new" className="btn btn-primary btn-sm">+ New Order</Link>
          </div>
          <div className="table-wrap" style={{ boxShadow: 'none', border: 'none' }}>
            <table className="data-table">
              <thead>
                <tr><th>#</th><th>Date</th><th>Items</th><th>Total</th><th>Status</th><th></th></tr>
              </thead>
              <tbody>
                {loadingO ? (
                  [...Array(3)].map((_, i) => <tr key={i}><td colSpan={6}><div className="skeleton" style={{ height: 18 }} /></td></tr>)
                ) : orders.length === 0 ? (
                  <tr><td colSpan={6}><div className="empty-state" style={{ padding: '40px 0' }}><h3>No orders yet</h3></div></td></tr>
                ) : orders.map(o => (
                  <tr key={o.id}>
                    <td style={{ fontWeight: 600 }}>#{o.id}</td>
                    <td style={{ fontSize: '0.8rem', color: 'var(--color-muted)' }}>{new Date(o.createdAt).toLocaleDateString('en-IN')}</td>
                    <td>{o.itemCount} item{o.itemCount !== 1 ? 's' : ''}</td>
                    <td style={{ fontWeight: 600 }}>₹{Number(o.total).toLocaleString('en-IN')}</td>
                    <td><span className={`badge ${STATUS_BADGE[o.status]}`}>{o.status}</span></td>
                    <td><Link to={`/orders/${o.id}`} className="btn btn-ghost btn-sm">View</Link></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
