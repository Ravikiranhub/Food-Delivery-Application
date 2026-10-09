import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import MetricGrid from '../../components/MetricGrid.jsx';
import { ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function AdminDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/admin/dashboard')
      .then(({ data }) => setDashboard(data))
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingState label="Loading admin dashboard" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">Admin</p>
          <h1>Platform dashboard</h1>
        </div>
        <div className="inline-actions">
          <Link className="secondary-button" to="/admin/users">Users</Link>
          <Link className="secondary-button" to="/admin/restaurants">Restaurants</Link>
          <Link className="secondary-button" to="/admin/orders">Orders</Link>
          <Link className="primary-link" to="/admin/coupons">Coupons</Link>
        </div>
      </div>
      <MetricGrid data={dashboard} />
    </section>
  );
}

