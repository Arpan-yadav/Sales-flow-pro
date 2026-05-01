import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useParams, Link } from 'react-router-dom';
import { orderAPI, invoiceAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { ArrowLeft, FileText, Download } from 'lucide-react';

const STATUS_BADGE  = { PENDING:'badge-amber', CONFIRMED:'badge-blue', SHIPPED:'badge-cyan', DELIVERED:'badge-green', CANCELLED:'badge-red' };
const NEXT_STATUSES = { PENDING:['CONFIRMED','CANCELLED'], CONFIRMED:['SHIPPED','CANCELLED'], SHIPPED:['DELIVERED','CANCELLED'] };

export default function OrderDetailPage() {
  const { id } = useParams();
  const qc = useQueryClient();

  const { data: order, isLoading } = useQuery({
    queryKey: ['order', id],
    queryFn: () => orderAPI.getById(id).then(r => r.data.data),
  });

  const updateStatus = useMutation({
    mutationFn: status => orderAPI.updateStatus(id, status),
    onSuccess: () => { qc.invalidateQueries(['order', id]); toast.success('Status updated!'); },
  });

  const generateInvoice = useMutation({
    mutationFn: () => invoiceAPI.generate(id),
    onSuccess: () => { qc.invalidateQueries(['order', id]); toast.success('Invoice generated!'); },
    onError:   () => toast.error('Failed to generate invoice'),
  });

  const downloadInvoice = async () => {
    try {
      const res = await invoiceAPI.download(order.invoiceId);
      const url = URL.createObjectURL(res.data);
      const a   = document.createElement('a'); a.href = url; a.download = `invoice-${order.invoiceNumber}.pdf`; a.click();
      URL.revokeObjectURL(url);
    } catch { toast.error('Download failed'); }
  };

  if (isLoading) return <div className="skeleton" style={{ height: 300, borderRadius: 'var(--radius-lg)' }} />;

  const nextStatuses = NEXT_STATUSES[order?.status] || [];

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <Link to="/orders" className="btn btn-ghost btn-sm"><ArrowLeft size={15} /> Back</Link>
        <div style={{ display: 'flex', gap: 10 }}>
          {order?.invoiceId ? (
            <button className="btn btn-secondary" onClick={downloadInvoice}><Download size={15} /> Download Invoice</button>
          ) : order?.status === 'DELIVERED' && (
            <button className="btn btn-secondary" onClick={() => generateInvoice.mutate()} disabled={generateInvoice.isPending}>
              <FileText size={15} /> {generateInvoice.isPending ? 'Generating…' : 'Generate Invoice'}
            </button>
          )}
          {nextStatuses.map(s => (
            <button key={s} className={`btn ${s === 'CANCELLED' ? 'btn-danger' : 'btn-primary'} btn-sm`}
              onClick={() => updateStatus.mutate(s)} disabled={updateStatus.isPending}>
              Move to {s}
            </button>
          ))}
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: 20 }}>
        {/* Items */}
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Order #{order?.id}</div>
              <div className="card-subtitle">{new Date(order?.createdAt).toLocaleString('en-IN')}</div>
            </div>
            <span className={`badge ${STATUS_BADGE[order?.status]}`}>{order?.status}</span>
          </div>

          <div className="table-wrap" style={{ boxShadow: 'none', border: 'none' }}>
            <table className="data-table">
              <thead><tr><th>Product</th><th>Unit Price</th><th>Qty</th><th>Line Total</th></tr></thead>
              <tbody>
                {order?.items?.map(it => (
                  <tr key={it.id}>
                    <td style={{ fontWeight: 600 }}>{it.productName}</td>
                    <td>₹{Number(it.unitPrice).toLocaleString('en-IN')}</td>
                    <td>{it.quantity}</td>
                    <td style={{ fontWeight: 700 }}>₹{Number(it.lineTotal).toLocaleString('en-IN')}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {order?.notes && (
            <div style={{ marginTop: 16, padding: '12px 14px', background: 'var(--color-bg-alt)', borderRadius: 'var(--radius-sm)', fontSize: '0.85rem', color: 'var(--color-muted)' }}>
              📝 {order.notes}
            </div>
          )}
        </div>

        {/* Side summary */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {/* Customer */}
          <div className="card">
            <div className="card-title" style={{ marginBottom: 12 }}>Customer</div>
            <div style={{ fontWeight: 700 }}>{order?.customerName}</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--color-muted)', marginTop: 4 }}>{order?.customerEmail}</div>
            <Link to={`/customers/${order?.customerId}`} className="btn btn-ghost btn-sm" style={{ marginTop: 10, width: '100%' }}>View Profile</Link>
          </div>

          {/* Pricing */}
          <div className="card">
            <div className="card-title" style={{ marginBottom: 12 }}>Pricing</div>
            {[
              ['Subtotal',  `₹${Number(order?.subtotal).toLocaleString('en-IN')}`],
              [`Discount (${order?.discountPercent}%)`, `-₹${Number(order?.discountAmount).toLocaleString('en-IN')}`],
              [`Tax (${order?.taxPercent}%)`, `₹${Number(order?.taxAmount).toLocaleString('en-IN')}`],
            ].map(([l, v]) => (
              <div key={l} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', marginBottom: 8, color: 'var(--color-muted)' }}>
                <span>{l}</span><span>{v}</span>
              </div>
            ))}
            <div className="divider" />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 800, fontSize: '1.1rem' }}>
              <span>Total</span>
              <span style={{ color: 'var(--color-primary)' }}>₹{Number(order?.total).toLocaleString('en-IN')}</span>
            </div>
          </div>

          {/* Salesperson */}
          <div className="card">
            <div className="card-title" style={{ marginBottom: 8 }}>Salesperson</div>
            <div style={{ fontWeight: 600 }}>{order?.salespersonName}</div>
          </div>
        </div>
      </div>
    </div>
  );
}
