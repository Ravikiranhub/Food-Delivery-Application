import { Minus, Plus, Trash2 } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    setLoading(true);
    try {
      const { data } = await api.get('/cart');
      setCart(data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const update = async (item, quantity) => {
    if (quantity < 1) return;
    const { data } = await api.put(`/cart/items/${item.id}`, { quantity });
    setCart(data);
  };

  const remove = async (id) => {
    const { data } = await api.delete(`/cart/items/${id}`);
    setCart(data);
  };

  if (loading) return <LoadingState label="Loading cart" />;
  if (error) return <ErrorState message={error} />;
  if (!cart?.items?.length) return <EmptyState title="Your cart is empty" detail="Add food from a restaurant to start checkout." />;

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">{cart.restaurantName}</p>
          <h1>Your cart</h1>
        </div>
        <Link className="primary-link" to="/checkout">Checkout</Link>
      </div>
      <div className="split-layout">
        <div className="panel-list">
          {cart.items.map((item) => (
            <article className="line-item" key={item.id}>
              <div className="food-thumb" style={{ backgroundImage: `url(${item.imageUrl || '/food-placeholder.svg'})` }} />
              <div>
                <h3>{item.foodName}</h3>
                <p>₹{item.unitPrice} · ₹{item.itemTotal}</p>
              </div>
              <div className="quantity-control">
                <button onClick={() => update(item, item.quantity - 1)}><Minus size={16} /></button>
                <span>{item.quantity}</span>
                <button onClick={() => update(item, item.quantity + 1)}><Plus size={16} /></button>
              </div>
              <button className="icon-button danger" onClick={() => remove(item.id)}><Trash2 size={17} /></button>
            </article>
          ))}
        </div>
        <OrderSummary cart={cart} />
      </div>
    </section>
  );
}

function OrderSummary({ cart }) {
  return (
    <aside className="summary-panel">
      <h2>Summary</h2>
      <p><span>Subtotal</span><strong>₹{cart.subtotal}</strong></p>
      <p><span>Delivery fee</span><strong>₹{cart.deliveryFee}</strong></p>
      <p><span>Tax</span><strong>₹{cart.taxAmount}</strong></p>
      <p className="total"><span>Total</span><strong>₹{cart.totalAmount}</strong></p>
    </aside>
  );
}

