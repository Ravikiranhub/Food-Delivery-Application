import { useEffect, useState } from 'react';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage, pageContent } from '../../services/api.js';

const steps = ['PLACED', 'CONFIRMED', 'PREPARING', 'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY', 'DELIVERED'];

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const { data } = await api.get('/orders', { params: { size: 50 } });
      setOrders(pageContent(data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const cancel = async (id) => {
    await api.post(`/orders/${id}/cancel`);
    load();
  };

  if (loading) return <LoadingState label="Loading orders" />;
  if (error) return <ErrorState message={error} />;
  if (!orders.length) return <EmptyState title="No orders yet" detail="Your placed orders will appear here." />;

  return (
    <section className="page-stack">
      <h1>Orders</h1>
      <div className="panel-list">
        {orders.map((order) => (
          <article className="order-card" key={order.id}>
            <div className="card-title-row">
              <div>
                <h3>{order.restaurantName}</h3>
                <p>{order.orderNumber} · ₹{order.totalAmount}</p>
              </div>
              <span className="status-pill">{order.status}</span>
            </div>
            <div className="timeline">
              {steps.map((step) => (
                <span key={step} className={steps.indexOf(step) <= steps.indexOf(order.status) ? 'done' : ''}>{step.replaceAll('_', ' ')}</span>
              ))}
            </div>
            <div className="inline-actions">
              {(order.status === 'PLACED' || order.status === 'CONFIRMED') && (
                <button className="secondary-button danger-text" onClick={() => cancel(order.id)}>Cancel</button>
              )}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

