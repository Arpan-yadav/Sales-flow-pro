import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { userAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Edit2, ToggleLeft, ToggleRight, Trash2 } from 'lucide-react';

const ROLE_BADGE = { ADMIN:'badge-red', SALES_MANAGER:'badge-blue', SALESPERSON:'badge-green' };

export default function UsersPage() {
  const [showModal, setShowModal] = useState(false);
  const [editUser, setEditUser]   = useState(null);
  const qc = useQueryClient();

  const { data: users = [], isLoading } = useQuery({
    queryKey: ['users'],
    queryFn: () => userAPI.getAll().then(r => r.data.data),
  });

  const toggleMut = useMutation({
    mutationFn: id => userAPI.toggle(id),
    onSuccess: () => { qc.invalidateQueries(['users']); toast.success('User status toggled'); },
  });

  const deleteMut = useMutation({
    mutationFn: id => userAPI.delete(id),
    onSuccess: () => { qc.invalidateQueries(['users']); toast.success('User deleted'); },
    onError:   () => toast.error('Cannot delete this user'),
  });

  return (
    <div>
      <div className="toolbar">
        <div style={{ flex: 1 }} />
        <button className="btn btn-primary" onClick={() => { setEditUser(null); setShowModal(true); }}>
          <Plus size={16} /> Add User
        </button>
      </div>

      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr><th>User</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Joined</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {isLoading ? (
              [...Array(4)].map((_, i) => <tr key={i}><td colSpan={7}><div className="skeleton" style={{ height: 20 }} /></td></tr>)
            ) : users.map(u => (
              <tr key={u.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div className="avatar" style={{ width: 32, height: 32, fontSize: '0.75rem' }}>
                      {u.name?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()}
                    </div>
                    <span style={{ fontWeight: 600 }}>{u.name}</span>
                  </div>
                </td>
                <td>{u.email}</td>
                <td>{u.phone || '—'}</td>
                <td><span className={`badge ${ROLE_BADGE[u.role]}`}>{u.role?.replace('_', ' ')}</span></td>
                <td><span className={`badge ${u.active ? 'badge-green' : 'badge-gray'}`}>{u.active ? 'Active' : 'Inactive'}</span></td>
                <td style={{ fontSize: '0.8rem', color: 'var(--color-muted)' }}>{new Date(u.createdAt).toLocaleDateString('en-IN')}</td>
                <td>
                  <div className="actions">
                    <button className="btn btn-ghost btn-icon btn-sm" onClick={() => { setEditUser(u); setShowModal(true); }}><Edit2 size={15} /></button>
                    <button className="btn btn-ghost btn-icon btn-sm" onClick={() => toggleMut.mutate(u.id)} title={u.active ? 'Deactivate' : 'Activate'}>
                      {u.active ? <ToggleRight size={18} color="var(--color-success)" /> : <ToggleLeft size={18} color="var(--color-muted)" />}
                    </button>
                    <button className="btn btn-ghost btn-icon btn-sm" style={{ color: 'var(--color-danger)' }} onClick={() => { if(confirm(`Delete ${u.name}?`)) deleteMut.mutate(u.id); }}>
                      <Trash2 size={15} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <UserModal user={editUser} onClose={() => setShowModal(false)} onSaved={() => { setShowModal(false); qc.invalidateQueries(['users']); }} />
      )}
    </div>
  );
}

function UserModal({ user, onClose, onSaved }) {
  const isEdit = !!user;
  const [form, setForm] = useState({ name: user?.name || '', email: user?.email || '', password: '', role: user?.role || 'SALESPERSON', phone: user?.phone || '' });
  const [loading, setLoading] = useState(false);
  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async e => {
    e.preventDefault(); setLoading(true);
    try {
      const payload = { ...form };
      if (isEdit && !payload.password) delete payload.password;
      if (isEdit) await userAPI.update(user.id, payload);
      else        await userAPI.create(payload);
      toast.success(`User ${isEdit ? 'updated' : 'created'}!`);
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save');
    } finally { setLoading(false); }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h3 className="modal-title">{isEdit ? 'Edit User' : 'Add User'}</h3>
          <button className="btn btn-ghost btn-icon" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-row">
              <div className="form-group"><label className="form-label">Full Name *</label><input className="form-input" required value={form.name} onChange={e => set('name', e.target.value)} /></div>
              <div className="form-group"><label className="form-label">Email *</label><input className="form-input" type="email" required value={form.email} onChange={e => set('email', e.target.value)} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label className="form-label">{isEdit ? 'New Password' : 'Password *'}</label><input className="form-input" type="password" required={!isEdit} value={form.password} onChange={e => set('password', e.target.value)} placeholder={isEdit ? 'Leave blank to keep' : ''} /></div>
              <div className="form-group"><label className="form-label">Role</label>
                <select className="form-select" value={form.role} onChange={e => set('role', e.target.value)}>
                  <option value="SALESPERSON">Salesperson</option>
                  <option value="SALES_MANAGER">Sales Manager</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
            </div>
            <div className="form-group"><label className="form-label">Phone</label><input className="form-input" value={form.phone} onChange={e => set('phone', e.target.value)} /></div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Saving…' : isEdit ? 'Update User' : 'Add User'}</button>
          </div>
        </form>
      </div>
    </div>
  );
}
