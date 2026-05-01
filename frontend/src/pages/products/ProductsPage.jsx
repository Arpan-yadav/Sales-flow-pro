import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productAPI, categoryAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Search, Edit2, Trash2, Download, Upload, AlertTriangle } from 'lucide-react';

export default function ProductsPage() {
  const [search, setSearch]         = useState('');
  const [catFilter, setCatFilter]   = useState('');
  const [showModal, setShowModal]   = useState(false);
  const [editProduct, setEditProduct] = useState(null);
  const qc = useQueryClient();

  const { data: products = [], isLoading } = useQuery({
    queryKey: ['products', search, catFilter],
    queryFn: () => productAPI.getAll({ search: search || undefined, category: catFilter || undefined }).then(r => r.data.data),
  });

  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryAPI.getAll().then(r => r.data.data),
  });

  const deleteMut = useMutation({
    mutationFn: id => productAPI.delete(id),
    onSuccess: () => { qc.invalidateQueries(['products']); toast.success('Product deleted'); },
    onError:   () => toast.error('Failed to delete product'),
  });

  const handleExport = async () => {
    try {
      const res = await productAPI.exportCsv();
      const url = URL.createObjectURL(res.data);
      const a = document.createElement('a'); a.href = url; a.download = 'products.csv'; a.click();
      URL.revokeObjectURL(url);
    } catch { toast.error('Export failed'); }
  };

  return (
    <div>
      {/* Toolbar */}
      <div className="toolbar">
        <div className="search-wrap">
          <Search size={16} className="search-icon" />
          <input className="search-input" placeholder="Search products or SKU…" value={search} onChange={e => setSearch(e.target.value)} />
        </div>
        <select className="form-select" style={{ width: 180 }} value={catFilter} onChange={e => setCatFilter(e.target.value)}>
          <option value="">All Categories</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <button className="btn btn-secondary btn-sm" onClick={handleExport} title="Export CSV"><Download size={15} /> Export</button>
        <button className="btn btn-primary" onClick={() => { setEditProduct(null); setShowModal(true); }}>
          <Plus size={16} /> Add Product
        </button>
      </div>

      {/* Table */}
      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr>
              <th>Product</th>
              <th>SKU</th>
              <th>Category</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              [...Array(5)].map((_, i) => (
                <tr key={i}><td colSpan={7}><div className="skeleton" style={{ height: 20, borderRadius: 4 }} /></td></tr>
              ))
            ) : products.length === 0 ? (
              <tr><td colSpan={7}>
                <div className="empty-state">
                  <Package size={40} />
                  <h3>No products found</h3>
                  <p>Add your first product to get started</p>
                </div>
              </td></tr>
            ) : products.map(p => (
              <tr key={p.id}>
                <td>
                  <div style={{ fontWeight: 600, color: 'var(--color-text)' }}>{p.name}</div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--color-muted)' }}>{p.description}</div>
                </td>
                <td><span className="font-mono badge badge-gray">{p.sku || '—'}</span></td>
                <td>{p.categoryName || '—'}</td>
                <td style={{ fontWeight: 600 }}>₹{Number(p.price).toLocaleString('en-IN')}</td>
                <td>
                  <span style={{ display:'flex', alignItems:'center', gap:4 }}>
                    {p.lowStock && <AlertTriangle size={14} color="var(--color-warning)" />}
                    {p.stockQuantity}
                  </span>
                </td>
                <td>
                  <span className={`badge ${p.lowStock ? 'badge-amber' : p.stockQuantity === 0 ? 'badge-red' : 'badge-green'}`}>
                    {p.stockQuantity === 0 ? 'Out of Stock' : p.lowStock ? 'Low Stock' : 'In Stock'}
                  </span>
                </td>
                <td>
                  <div className="actions">
                    <button className="btn btn-ghost btn-icon btn-sm" onClick={() => { setEditProduct(p); setShowModal(true); }} title="Edit">
                      <Edit2 size={15} />
                    </button>
                    <button className="btn btn-ghost btn-icon btn-sm" style={{ color: 'var(--color-danger)' }}
                      onClick={() => { if (confirm(`Delete "${p.name}"?`)) deleteMut.mutate(p.id); }} title="Delete">
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
        <ProductModal
          product={editProduct}
          categories={categories}
          onClose={() => setShowModal(false)}
          onSaved={() => { setShowModal(false); qc.invalidateQueries(['products']); }}
        />
      )}
    </div>
  );
}

function ProductModal({ product, categories, onClose, onSaved }) {
  const isEdit = !!product;
  const [form, setForm] = useState({
    name: product?.name || '',
    sku: product?.sku || '',
    description: product?.description || '',
    price: product?.price || '',
    stockQuantity: product?.stockQuantity ?? '',
    lowStockThreshold: product?.lowStockThreshold ?? 10,
    categoryId: product?.categoryId || '',
  });
  const [loading, setLoading] = useState(false);

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    try {
      if (isEdit) await productAPI.update(product.id, form);
      else        await productAPI.create(form);
      toast.success(`Product ${isEdit ? 'updated' : 'created'}!`);
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save product');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h3 className="modal-title">{isEdit ? 'Edit Product' : 'Add New Product'}</h3>
          <button className="btn btn-ghost btn-icon" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Product Name *</label>
                <input className="form-input" required value={form.name} onChange={e => set('name', e.target.value)} placeholder="e.g. Laptop Pro X1" />
              </div>
              <div className="form-group">
                <label className="form-label">SKU</label>
                <input className="form-input" value={form.sku} onChange={e => set('sku', e.target.value)} placeholder="e.g. LPX1-001" />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Price (₹) *</label>
                <input className="form-input" type="number" required min="0" step="0.01" value={form.price} onChange={e => set('price', e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">Category</label>
                <select className="form-select" value={form.categoryId} onChange={e => set('categoryId', e.target.value)}>
                  <option value="">Select category</option>
                  {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Stock Quantity *</label>
                <input className="form-input" type="number" required min="0" value={form.stockQuantity} onChange={e => set('stockQuantity', e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">Low Stock Alert At</label>
                <input className="form-input" type="number" min="0" value={form.lowStockThreshold} onChange={e => set('lowStockThreshold', e.target.value)} />
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Description</label>
              <textarea className="form-textarea" value={form.description} onChange={e => set('description', e.target.value)} placeholder="Optional product description" rows={2} />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving…' : isEdit ? 'Update Product' : 'Add Product'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
