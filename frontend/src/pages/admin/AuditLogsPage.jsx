import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { auditAPI } from '../../services/api';

const ACTION_BADGE = { CREATE: 'badge-green', UPDATE: 'badge-blue', DELETE: 'badge-red' };

export default function AuditLogsPage() {
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ['audit-logs', page],
    queryFn: () => auditAPI.getAll({ page, size: 20 }).then(r => r.data.data),
  });

  const logs = data?.content || [];
  const totalPages = data?.totalPages || 0;

  return (
    <div>
      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr><th>Time</th><th>User</th><th>Action</th><th>Entity</th><th>Entity ID</th><th>Details</th></tr>
          </thead>
          <tbody>
            {isLoading ? (
              [...Array(5)].map((_, i) => <tr key={i}><td colSpan={6}><div className="skeleton" style={{ height: 18 }} /></td></tr>)
            ) : logs.length === 0 ? (
              <tr><td colSpan={6}><div className="empty-state"><h3>No audit logs yet</h3></div></td></tr>
            ) : logs.map(log => (
              <tr key={log.id}>
                <td style={{ fontSize: '0.78rem', color: 'var(--color-muted)', whiteSpace: 'nowrap' }}>
                  {new Date(log.timestamp).toLocaleString('en-IN')}
                </td>
                <td style={{ fontWeight: 600 }}>{log.userName}</td>
                <td><span className={`badge ${ACTION_BADGE[log.action] || 'badge-gray'}`}>{log.action}</span></td>
                <td><span className="badge badge-purple">{log.entity}</span></td>
                <td style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>#{log.entityId}</td>
                <td style={{ fontSize: '0.8rem', color: 'var(--color-muted)', maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{log.details}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 20 }}>
          <button className="btn btn-secondary btn-sm" disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Prev</button>
          <span style={{ padding: '6px 14px', fontSize: '0.875rem', color: 'var(--color-muted)' }}>Page {page + 1} / {totalPages}</span>
          <button className="btn btn-secondary btn-sm" disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next →</button>
        </div>
      )}
    </div>
  );
}
