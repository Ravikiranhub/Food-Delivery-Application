import { useEffect, useState } from 'react';
import { ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage, pageContent } from '../../services/api.js';

export default function AdminRestaurants() {
  const [restaurants, setRestaurants] = useState([]);
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, [status]);

  const load = async () => {
    setLoading(true);
    try {
      const { data } = await api.get('/admin/restaurants', { params: { status: status || undefined, size: 100 } });
      setRestaurants(pageContent(data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const action = async (id, path) => {
    await api.patch(`/admin/restaurants/${id}/${path}`);
    load();
  };

  if (loading) return <LoadingState label="Loading restaurants" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <div className="toolbar">
        <h1>Restaurants</h1>
        <select value={status} onChange={(event) => setStatus(event.target.value)}>
          <option value="">All statuses</option>
          <option value="PENDING_APPROVAL">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
          <option value="SUSPENDED">Suspended</option>
        </select>
      </div>
      <div className="table-wrap">
        <table>
          <thead><tr><th>Name</th><th>Owner</th><th>Cuisine</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {restaurants.map((restaurant) => (
              <tr key={restaurant.id}>
                <td>{restaurant.name}</td>
                <td>{restaurant.ownerName}</td>
                <td>{restaurant.cuisineType}</td>
                <td>{restaurant.status}</td>
                <td className="table-actions">
                  <button onClick={() => action(restaurant.id, 'approve')}>Approve</button>
                  <button onClick={() => action(restaurant.id, 'reject')}>Reject</button>
                  <button onClick={() => action(restaurant.id, 'suspend')}>Suspend</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

