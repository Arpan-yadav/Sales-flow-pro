import { useLocation } from 'react-router-dom';
import { Bell } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { productAPI } from '../../services/api';

const PAGE_TITLES = {
  '/':            { title: 'Dashboard',   sub: 'Overview of your business performance' },
  '/products':    { title: 'Products',    sub: 'Manage your product catalog & inventory' },
  '/customers':   { title: 'Customers',   sub: 'View and manage customer relationships' },
  '/orders':      { title: 'Orders',      sub: 'Track and process sales orders' },
  '/orders/new':  { title: 'New Order',   sub: 'Create a new sales order' },
  '/invoices':    { title: 'Invoices',    sub: 'View and download generated invoices' },
  '/reports':     { title: 'Reports',     sub: 'Analytics and business performance reports' },
  '/users':       { title: 'User Management', sub: 'Manage system users and access' },
  '/audit-logs':  { title: 'Audit Logs',  sub: 'Track all system activity' },
};

export default function TopHeader() {
  const { pathname } = useLocation();
  const page = PAGE_TITLES[pathname] || { title: 'SalesFlow Pro', sub: '' };

  const { data: lowStockData } = useQuery({
    queryKey: ['low-stock'],
    queryFn: () => productAPI.getLowStock().then(r => r.data.data),
    refetchInterval: 60_000,
  });

  const lowStockCount = lowStockData?.length || 0;

  return (
    <header className="top-header">
      <div className="page-title-area">
        <h1>{page.title}</h1>
        {page.sub && <p>{page.sub}</p>}
      </div>

      <div className="header-actions">
        {/* Low stock bell */}
        <div style={{ position: 'relative' }}>
          <button className="btn btn-ghost btn-icon" title="Low stock alerts">
            <Bell size={20} />
          </button>
          {lowStockCount > 0 && (
            <span style={{
              position: 'absolute', top: 4, right: 4,
              width: 16, height: 16, borderRadius: '50%',
              background: '#DC2626', color: 'white',
              fontSize: '0.6rem', fontWeight: 700,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              {lowStockCount > 9 ? '9+' : lowStockCount}
            </span>
          )}
        </div>

        {/* Current date */}
        <span style={{ fontSize: '0.8rem', color: 'var(--color-muted)', borderLeft: '1px solid var(--color-border)', paddingLeft: 16 }}>
          {new Date().toLocaleDateString('en-IN', { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' })}
        </span>
      </div>
    </header>
  );
}
