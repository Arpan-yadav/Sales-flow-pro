import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import TopHeader from './TopHeader';

export default function AppLayout() {
  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <TopHeader />
        <main className="page-container">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
