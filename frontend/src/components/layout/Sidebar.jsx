import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard, Package, Users, ShoppingCart,
  FileText, BarChart2, UserCog, ClipboardList, LogOut
} from 'lucide-react';

const NAV = [
  { to: '/',          label: 'Dashboard',   icon: LayoutDashboard },
  { to: '/products',  label: 'Products',    icon: Package },
  { to: '/customers', label: 'Customers',   icon: Users },
  { to: '/orders',    label: 'Orders',      icon: ShoppingCart },
  { to: '/invoices',  label: 'Invoices',    icon: FileText },
  { to: '/reports',   label: 'Reports',     icon: BarChart2,    managerOnly: true },
];

const ADMIN_NAV = [
  { to: '/users',      label: 'Users',       icon: UserCog },
  { to: '/audit-logs', label: 'Audit Logs',  icon: ClipboardList },
];

export default function Sidebar() {
  const { user, logout, isAdmin, isManager } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const initials = user?.name?.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) || '?';

  return (
    <aside className="sidebar">
      {/* Logo */}
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">💼</div>
        <div>
          <div className="sidebar-logo-text">SalesFlow</div>
          <span className="sidebar-logo-sub">Pro Edition</span>
        </div>
      </div>

      {/* Main Nav */}
      <nav className="sidebar-nav">
        <div className="sidebar-section-label">Main Menu</div>
        {NAV.filter(item => !item.managerOnly || isManager()).map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
          >
            <Icon size={18} />
            <span>{label}</span>
          </NavLink>
        ))}

        {/* Admin Nav */}
        {isAdmin() && (
          <>
            <div className="sidebar-section-label" style={{ marginTop: 8 }}>Administration</div>
            {ADMIN_NAV.map(({ to, label, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
              >
                <Icon size={18} />
                <span>{label}</span>
              </NavLink>
            ))}
          </>
        )}
      </nav>

      {/* User Footer */}
      <div className="sidebar-footer">
        <div className="sidebar-user">
          <div className="sidebar-user-avatar">{initials}</div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div className="sidebar-user-name">{user?.name}</div>
            <div className="sidebar-user-role">{user?.role?.replace('_', ' ')}</div>
          </div>
          <button
            onClick={handleLogout}
            className="btn btn-ghost btn-icon"
            title="Logout"
            style={{ color: '#64748B' }}
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </aside>
  );
}
