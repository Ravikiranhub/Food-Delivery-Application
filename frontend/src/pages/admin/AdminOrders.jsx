import { useEffect, useState } from 'react';
import { ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage, pageContent } from '../../services/api.js';

export default function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    load();
  }, [status]);

  const load = async () => {
    setLoading(true);
    try {
      const { data } = await api.get('/admin/orders', { params: { status: status || undefined, size: 100 } });
      setOrders(pageContent(data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingState label="Loading orders" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <div className="toolbar">
        <h1>Orders</h1>
        <select value={status} onChange={(event) => setStatus(event.target.value)}>
          <option value="">All statuses</option>
          {['PLACED', 'CONFIRMED', 'PREPARING', 'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED', 'REJECTED'].map((item) => (
            <option key={item} value={item}>{item}</option>
          ))}
        </select>
      </div>
      <div className="table-wrap">
        <table>
          <thead><tr><th>Order</th><th>Customer</th><th>Restaurant</th><th>Status</th><th>Total</th></tr></thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td>{order.orderNumber}</td>
                <td>{order.customerName}</td>
                <td>{order.restaurantName}</td>
                <td>{order.status}</td>
                <td>₹{order.totalAmount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

