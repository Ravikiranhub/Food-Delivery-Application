import { useEffect, useState } from 'react';
import MetricGrid from '../../components/MetricGrid.jsx';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function DeliveryDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [available, setAvailable] = useState([]);
  const [assigned, setAssigned] = useState([]);
  const [history, setHistory] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const [dashboardRes, availableRes, assignedRes, historyRes] = await Promise.all([
        api.get('/delivery/dashboard'),
        api.get('/delivery/available-orders'),
        api.get('/delivery/assigned-orders'),
        api.get('/delivery/history')
      ]);
      setDashboard(dashboardRes.data);
      setAvailable(availableRes.data);
      setAssigned(assignedRes.data);
      setHistory(historyRes.data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const action = async (orderId, path) => {
    await api.post(`/delivery/orders/${orderId}/${path}`);
    load();
  };

  if (loading) return <LoadingState label="Loading delivery dashboard" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <div>
        <p className="eyebrow">Delivery partner</p>
        <h1>Dashboard</h1>
      </div>
      <MetricGrid data={dashboard} />
      <div className="split-layout">
        <section className="soft-panel">
          <h2>Available pickups</h2>
          {!available.length ? <EmptyState title="No pickups ready" detail="Ready orders will appear here." /> : available.map((delivery) => (
            <article className="line-item" key={delivery.id}>
              <div>
                <h3>{delivery.restaurantName}</h3>
                <p>{delivery.orderNumber} · {delivery.customerName}</p>
              </div>
              <button className="primary-button" onClick={() => action(delivery.orderId, 'accept')}>Accept</button>
            </article>
          ))}
        </section>
        <section className="soft-panel">
          <h2>Assigned</h2>
          {!assigned.length ? <EmptyState title="No active delivery" detail="Accepted deliveries appear here." /> : assigned.map((delivery) => (
            <article className="line-item" key={delivery.id}>
              <div>
                <h3>{delivery.orderNumber}</h3>
                <p>{delivery.status} · ₹{delivery.deliveryFee}</p>
              </div>
              {delivery.status === 'ACCEPTED' && <button className="secondary-button" onClick={() => action(delivery.orderId, 'picked-up')}>Picked up</button>}
              {delivery.status === 'PICKED_UP' && <button className="primary-button" onClick={() => action(delivery.orderId, 'delivered')}>Delivered</button>}
            </article>
          ))}
        </section>
      </div>
      <section className="soft-panel">
        <h2>History</h2>
        {!history.length ? <EmptyState title="No delivery history" detail="Completed deliveries will be listed here." /> : history.slice(0, 8).map((delivery) => (
          <p className="address-line" key={delivery.id}>{delivery.orderNumber} · {delivery.status} · ₹{delivery.deliveryFee}</p>
        ))}
      </section>
    </section>
  );
}

