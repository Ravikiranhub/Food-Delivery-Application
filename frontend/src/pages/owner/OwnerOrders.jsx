import { useEffect, useState } from 'react';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage, pageContent } from '../../services/api.js';

export default function OwnerOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const { data } = await api.get('/owner/orders', { params: { size: 60 } });
      setOrders(pageContent(data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const update = async (order, status) => {
    await api.put(`/owner/orders/${order.id}/status`, { status, note: `Moved to ${status}` });
    load();
  };

  if (loading) return <LoadingState label="Loading orders" />;
  if (error) return <ErrorState message={error} />;
  if (!orders.length) return <EmptyState title="No incoming orders" detail="Customer orders will arrive here." />;

  return (
    <section className="page-stack">
      <h1>Restaurant orders</h1>
      <div className="panel-list">
        {orders.map((order) => (
          <article className="order-card" key={order.id}>
            <div className="card-title-row">
              <div>
                <h3>{order.orderNumber}</h3>
                <p>{order.customerName} · ₹{order.totalAmount}</p>
              </div>
              <span className="status-pill">{order.status}</span>
            </div>
            <div className="inline-actions">
              {order.status === 'PLACED' && (
                <>
                  <button className="primary-button" onClick={() => update(order, 'CONFIRMED')}>Accept</button>
                  <button className="secondary-button danger-text" onClick={() => update(order, 'REJECTED')}>Reject</button>
                </>
              )}
              {order.status === 'CONFIRMED' && <button className="secondary-button" onClick={() => update(order, 'PREPARING')}>Preparing</button>}
              {order.status === 'PREPARING' && <button className="secondary-button" onClick={() => update(order, 'READY_FOR_PICKUP')}>Ready</button>}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

