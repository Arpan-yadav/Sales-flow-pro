import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

export default function LoginPage() {
  const [email, setEmail]       = useState('admin@sales.com');
  const [password, setPassword] = useState('Admin@123');
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState('');
  const { login } = useAuth();
  const navigate  = useNavigate();

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(email, password);
      toast.success('Welcome back! 👋');
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        {/* Logo */}
        <div className="auth-logo">
          <div className="auth-logo-icon">💼</div>
        </div>

        <h2>Welcome Back</h2>
        <p className="auth-subtitle">Sign in to your SalesFlow Pro account</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              id="login-email"
              type="email"
              className={`form-input ${error ? 'error' : ''}`}
              placeholder="admin@sales.com"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              id="login-password"
              type="password"
              className={`form-input ${error ? 'error' : ''}`}
              placeholder="••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>

          {error && (
            <div style={{
              background: 'var(--color-danger-light)', color: 'var(--color-danger)',
              borderRadius: 'var(--radius-sm)', padding: '10px 14px',
              fontSize: '0.85rem', marginBottom: 'var(--space-4)',
              display: 'flex', alignItems: 'center', gap: 8,
            }}>
              ⚠️ {error}
            </div>
          )}

          <button id="login-submit" type="submit" className="btn btn-primary btn-lg" style={{ width: '100%' }} disabled={loading}>
            {loading ? <span className="spin" style={{ display: 'inline-block', width: 18, height: 18, border: '2px solid rgba(255,255,255,0.3)', borderTopColor: 'white', borderRadius: '50%' }} /> : null}
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        {/* Demo credentials hint */}
        <div style={{
          marginTop: 'var(--space-6)', padding: 'var(--space-4)',
          background: 'var(--color-primary-50)', borderRadius: 'var(--radius-md)',
          border: '1px solid var(--color-primary-light)', fontSize: '0.78rem',
          color: 'var(--color-primary-dark)',
        }}>
          <strong>Demo:</strong> admin@sales.com / Admin@123
        </div>
      </div>
    </div>
  );
}
