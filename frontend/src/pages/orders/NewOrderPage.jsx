import { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { customerAPI, productAPI, orderAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Trash2, ArrowLeft, Search } from 'lucide-react';

export default function NewOrderPage() {
  const { user } = useAuth();
  const navigate  = useNavigate();
  const [customerId,    setCustomerId]   = useState('');
  const [discountPct,   setDiscount]     = useState(0);
  const [taxPct,        setTax]          = useState(18);
  const [notes,         setNotes]        = useState('');
  const [items,         setItems]        = useState([]);   // [{productId, name, unitPrice, quantity}]
  const [productSearch, setProductSearch]= useState('');

  const { data: customers = [] } = useQuery({ queryKey: ['customers'], queryFn: () => customerAPI.getAll().then(r => r.data.data) });
  const { data: products  = [] } = useQuery({
    queryKey: ['products', productSearch],
    queryFn: () => productAPI.getAll({ search: productSearch || undefined }).then(r => r.data.data),
    enabled: productSearch.length > 0 || true,
  });

  const subtotal      = items.reduce((s, it) => s + Number(it.unitPrice) * it.quantity, 0);
  const discountAmount= subtotal * (discountPct / 100);
  const taxAmount     = (subtotal - discountAmount) * (taxPct / 100);
  const total         = subtotal - discountAmount + taxAmount;

  const addItem = product => {
    setItems(prev => {
      const existing = prev.find(i => i.productId === product.id);
      if (existing) return prev.map(i => i.productId === product.id ? { ...i, quantity: i.quantity + 1 } : i);
      return [...prev, { productId: product.id, name: product.name, unitPrice: product.price, quantity: 1 }];
    });
  };

  const removeItem  = id => setItems(p => p.filter(i => i.productId !== id));
  const setQty      = (id, q) => setItems(p => p.map(i => i.productId === id ? { ...i, quantity: Math.max(1, q) } : i));

  const createOrder = useMutation({
    mutationFn: () => orderAPI.create({
      customerId: Number(customerId),
      salespersonId: user.userId,
      discountPercent: discountPct,
      taxPercent: taxPct,
      notes,
      items: items.map(i => ({ productId: i.productId, quantity: i.quantity, unitPrice: i.unitPrice })),
    }),
    onSuccess: res => {
      toast.success('Order created! 🎉');
      navigate(`/orders/${res.data.data.id}`);
    },
    onError: err => toast.error(err.response?.data?.message || 'Failed to create order'),
  });

  const handleSubmit = e => {
    e.preventDefault();
    if (!customerId) return toast.error('Please select a customer');
    if (items.length === 0) return toast.error('Add at least one product');
    createOrder.mutate();
  };

  return (
    <div>
      <button className="btn btn-ghost btn-sm" onClick={() => navigate('/orders')} style={{ marginBottom: 20 }}>
        <ArrowLeft size={15} /> Back to Orders
      </button>

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 360px', gap: 20, alignItems: 'start' }}>
          {/* Left — Customer + Items */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {/* Customer */}
            <div className="card">
              <div className="card-title" style={{ marginBottom: 12 }}>Customer *</div>
              <select className="form-select" required value={customerId} onChange={e => setCustomerId(e.target.value)}>
                <option value="">Select a customer…</option>
                {customers.map(c => <option key={c.id} value={c.id}>{c.name} — {c.phone || c.email}</option>)}
              </select>
            </div>

            {/* Product search & add */}
            <div className="card">
              <div className="card-title" style={{ marginBottom: 12 }}>Add Products</div>
              <div className="search-wrap" style={{ marginBottom: 12 }}>
                <Search size={16} className="search-icon" />
                <input className="search-input" placeholder="Search products to add…" value={productSearch} onChange={e => setProductSearch(e.target.value)} />
              </div>
              <div style={{ maxHeight: 200, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 6 }}>
                {products.slice(0, 8).map(p => (
                  <div key={p.id} style={{ display:'flex', alignItems:'center', justifyContent:'space-between', padding:'8px 10px', border:'1px solid var(--color-border)', borderRadius:'var(--radius-sm)', background:'var(--color-bg-alt)' }}>
                    <div>
                      <div style={{ fontWeight: 600, fontSize: '0.875rem' }}>{p.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--color-muted)' }}>₹{Number(p.price).toLocaleString('en-IN')} · Stock: {p.stockQuantity}</div>
                    </div>
                    <button type="button" className="btn btn-primary btn-sm" onClick={() => addItem(p)} disabled={p.stockQuantity === 0}>
                      <Plus size={14} />
                    </button>
                  </div>
                ))}
              </div>
            </div>

            {/* Items table */}
            {items.length > 0 && (
              <div className="table-wrap">
                <table className="data-table">
                  <thead><tr><th>Product</th><th>Unit Price</th><th>Qty</th><th>Line Total</th><th></th></tr></thead>
                  <tbody>
                    {items.map(it => (
                      <tr key={it.productId}>
                        <td style={{ fontWeight: 600 }}>{it.name}</td>
                        <td>₹{Number(it.unitPrice).toLocaleString('en-IN')}</td>
                        <td>
                          <input type="number" min={1} value={it.quantity} style={{ width: 64, padding: '4px 8px', border: '1px solid var(--color-border)', borderRadius: 4, textAlign: 'center' }}
                            onChange={e => setQty(it.productId, Number(e.target.value))} />
                        </td>
                        <td style={{ fontWeight: 700 }}>₹{(Number(it.unitPrice) * it.quantity).toLocaleString('en-IN')}</td>
                        <td><button type="button" className="btn btn-ghost btn-icon btn-sm" style={{ color: 'var(--color-danger)' }} onClick={() => removeItem(it.productId)}><Trash2 size={14} /></button></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Right — Summary */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div className="card">
              <div className="card-title" style={{ marginBottom: 16 }}>Order Summary</div>
              <div className="form-group">
                <label className="form-label">Discount %</label>
                <input className="form-input" type="number" min={0} max={100} value={discountPct} onChange={e => setDiscount(Number(e.target.value))} />
              </div>
              <div className="form-group">
                <label className="form-label">Tax (GST) %</label>
                <input className="form-input" type="number" min={0} max={100} value={taxPct} onChange={e => setTax(Number(e.target.value))} />
              </div>
              <div className="form-group">
                <label className="form-label">Notes</label>
                <textarea className="form-textarea" rows={2} value={notes} onChange={e => setNotes(e.target.value)} placeholder="Optional order notes" />
              </div>

              <div className="divider" />
              {[
                { label: 'Subtotal',  value: subtotal },
                { label: `Discount (${discountPct}%)`, value: -discountAmount },
                { label: `Tax (${taxPct}%)`, value: taxAmount },
              ].map(({ label, value }) => (
                <div key={label} style={{ display:'flex', justifyContent:'space-between', fontSize:'0.875rem', marginBottom:8, color:'var(--color-muted)' }}>
                  <span>{label}</span>
                  <span style={{ color: value < 0 ? 'var(--color-success)' : 'var(--color-text-2)', fontWeight: 500 }}>
                    {value < 0 ? '-' : ''}₹{Math.abs(value).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </span>
                </div>
              ))}
              <div className="divider" />
              <div style={{ display:'flex', justifyContent:'space-between', fontWeight:800, fontSize:'1.1rem' }}>
                <span>Total</span>
                <span style={{ color: 'var(--color-primary)' }}>₹{total.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>

              <button type="submit" className="btn btn-primary btn-lg" style={{ width:'100%', marginTop:20 }} disabled={createOrder.isPending || items.length === 0}>
                {createOrder.isPending ? 'Placing…' : '✓ Place Order'}
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}
