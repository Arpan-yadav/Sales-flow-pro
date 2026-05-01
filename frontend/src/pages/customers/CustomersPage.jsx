import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { customerAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Search, Edit2, Trash2, Eye } from 'lucide-react';

export default function CustomersPage() {
  const [search, setSearch]         = useState('');
  const [showModal, setShowModal]   = useState(false);
  const [editCustomer, setEdit]     = useState(null);
  const qc = useQueryClient();

  const { data: customers = [], isLoading } = useQuery({
    queryKey: ['customers', search],
    queryFn: () => customerAPI.getAll({ search: search || undefined }).then(r => r.data.data),
  });

  const deleteMut = useMutation({
    mutationFn: id => customerAPI.delete(id),
    onSuccess: () => { qc.invalidateQueries(['customers']); toast.success('Customer deleted'); },
    onError:   () => toast.error('Failed to delete'),
  });

  return (
    <div>
      <div className="toolbar">
        <div className="search-wrap">
          <Search size={16} className="search-icon" />
          <input className="search-input" placeholder="Search by name, email, phone…" value={search} onChange={e => setSearch(e.target.value)} />
        </div>
        <button className="btn btn-primary" onClick={() => { setEdit(null); setShowModal(true); }}>
          <Plus size={16} /> Add Customer
        </button>
      </div>

      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr><th>Customer</th><th>Email</th><th>Phone</th><th>City</th><th>Since</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {isLoading ? (
              [...Array(4)].map((_, i) => <tr key={i}><td colSpan={6}><div className="skeleton" style={{ height: 20, borderRadius: 4 }} /></td></tr>)
            ) : customers.length === 0 ? (
              <tr><td colSpan={6}><div className="empty-state"><h3>No customers yet</h3><p>Add your first customer</p></div></td></tr>
            ) : customers.map(c => (
              <tr key={c.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div className="avatar" style={{ width: 32, height: 32, fontSize: '0.75rem' }}>
                      {c.name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()}
                    </div>
                    <span style={{ fontWeight: 600 }}>{c.name}</span>
                  </div>
                </td>
                <td>{c.email || '—'}</td>
                <td>{c.phone || '—'}</td>
                <td>{c.city || '—'}</td>
                <td style={{ color: 'var(--color-muted)', fontSize: '0.8rem' }}>
                  {new Date(c.createdAt).toLocaleDateString('en-IN')}
                </td>
                <td>
                  <div className="actions">
                    <Link to={`/customers/${c.id}`} className="btn btn-ghost btn-icon btn-sm" title="View details"><Eye size={15} /></Link>
                    <button className="btn btn-ghost btn-icon btn-sm" onClick={() => { setEdit(c); setShowModal(true); }} title="Edit"><Edit2 size={15} /></button>
                    <button className="btn btn-ghost btn-icon btn-sm" style={{ color: 'var(--color-danger)' }}
                      onClick={() => { if (confirm(`Delete ${c.name}?`)) deleteMut.mutate(c.id); }} title="Delete"><Trash2 size={15} /></button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <CustomerModal
          customer={editCustomer}
          onClose={() => setShowModal(false)}
          onSaved={() => { setShowModal(false); qc.invalidateQueries(['customers']); }}
        />
      )}
    </div>
  );
}

function CustomerModal({ customer, onClose, onSaved }) {
  const isEdit = !!customer;
  const [form, setForm] = useState({
    name: customer?.name || '', email: customer?.email || '',
    phone: customer?.phone || '', address: customer?.address || '',
    city: customer?.city || '', country: customer?.country || 'India',
  });
  const [loading, setLoading] = useState(false);
  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async e => {
    e.preventDefault(); setLoading(true);
    try {
      if (isEdit) await customerAPI.update(customer.id, form);
      else        await customerAPI.create(form);
      toast.success(`Customer ${isEdit ? 'updated' : 'added'}!`);
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save');
    } finally { setLoading(false); }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h3 className="modal-title">{isEdit ? 'Edit Customer' : 'Add Customer'}</h3>
          <button className="btn btn-ghost btn-icon" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-row">
              <div className="form-group"><label className="form-label">Full Name *</label><input className="form-input" required value={form.name} onChange={e => set('name', e.target.value)} /></div>
              <div className="form-group"><label className="form-label">Email</label><input className="form-input" type="email" value={form.email} onChange={e => set('email', e.target.value)} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label className="form-label">Phone</label><input className="form-input" value={form.phone} onChange={e => set('phone', e.target.value)} /></div>
              <div className="form-group"><label className="form-label">City</label><input className="form-input" value={form.city} onChange={e => set('city', e.target.value)} /></div>
            </div>
            <div className="form-group"><label className="form-label">Address</label><input className="form-input" value={form.address} onChange={e => set('address', e.target.value)} /></div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Saving…' : isEdit ? 'Update' : 'Add Customer'}</button>
          </div>
        </form>
      </div>
    </div>
  );
}
