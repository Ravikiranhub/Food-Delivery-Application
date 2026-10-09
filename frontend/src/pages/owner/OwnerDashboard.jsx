import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import MetricGrid from '../../components/MetricGrid.jsx';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function OwnerDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [restaurants, setRestaurants] = useState([]);
  const [form, setForm] = useState({ name: '', description: '', cuisineType: '', phone: '', email: '', imageUrl: '', city: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const [dashboardRes, restaurantRes] = await Promise.all([api.get('/owner/dashboard'), api.get('/owner/restaurants')]);
      setDashboard(dashboardRes.data);
      setRestaurants(restaurantRes.data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const create = async (event) => {
    event.preventDefault();
    await api.post('/restaurants', form);
    setForm({ name: '', description: '', cuisineType: '', phone: '', email: '', imageUrl: '', city: '' });
    load();
  };

  const toggleOpen = async (restaurant) => {
    await api.patch(`/restaurants/${restaurant.id}/open-status`, null, { params: { open: !restaurant.open } });
    load();
  };

  if (loading) return <LoadingState label="Loading owner dashboard" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">Restaurant owner</p>
          <h1>Dashboard</h1>
        </div>
        <div className="inline-actions">
          <Link className="secondary-button" to="/owner/food">Food management</Link>
          <Link className="primary-link" to="/owner/orders">Orders</Link>
        </div>
      </div>
      <MetricGrid data={dashboard} />
      <div className="split-layout">
        <section className="soft-panel">
          <h2>Your restaurants</h2>
          {!restaurants.length ? <EmptyState title="No restaurant profile" detail="Create one and wait for admin approval." /> : restaurants.map((restaurant) => (
            <article className="line-item" key={restaurant.id}>
              <div>
                <h3>{restaurant.name}</h3>
                <p>{restaurant.cuisineType} · {restaurant.city}</p>
              </div>
              <span className="status-pill">{restaurant.status}</span>
              <button className="secondary-button" disabled={restaurant.status !== 'APPROVED'} onClick={() => toggleOpen(restaurant)}>
                {restaurant.open ? 'Close' : 'Open'}
              </button>
            </article>
          ))}
        </section>
        <form className="soft-panel compact-form" onSubmit={create}>
          <h2>Create restaurant</h2>
          <input placeholder="Name" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
          <input placeholder="Cuisine" value={form.cuisineType} onChange={(event) => setForm({ ...form, cuisineType: event.target.value })} />
          <input placeholder="Phone" value={form.phone} onChange={(event) => setForm({ ...form, phone: event.target.value })} />
          <input placeholder="Email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
          <input placeholder="City" value={form.city} onChange={(event) => setForm({ ...form, city: event.target.value })} />
          <input placeholder="Image URL" value={form.imageUrl} onChange={(event) => setForm({ ...form, imageUrl: event.target.value })} />
          <textarea placeholder="Description" value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} />
          <button className="primary-button">Create</button>
        </form>
      </div>
    </section>
  );
}

