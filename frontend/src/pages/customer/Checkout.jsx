import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function Checkout() {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [addresses, setAddresses] = useState([]);
  const [addressId, setAddressId] = useState('');
  const [couponCode, setCouponCode] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CASH_ON_DELIVERY');
  const [addressForm, setAddressForm] = useState({ label: 'Home', line1: '', line2: '', city: '', state: '', pincode: '', defaultAddress: true });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const [cartRes, addressRes] = await Promise.all([api.get('/cart'), api.get('/customers/addresses')]);
      setCart(cartRes.data);
      setAddresses(addressRes.data);
      setAddressId(addressRes.data.find((item) => item.defaultAddress)?.id || addressRes.data[0]?.id || '');
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const saveAddress = async (event) => {
    event.preventDefault();
    const { data } = await api.post('/customers/addresses', addressForm);
    setAddresses([data, ...addresses]);
    setAddressId(data.id);
    setAddressForm({ label: 'Home', line1: '', line2: '', city: '', state: '', pincode: '', defaultAddress: false });
  };

  const applyCoupon = async () => {
    try {
      const { data } = await api.post('/cart/apply-coupon', { code: couponCode });
      setMessage(`Coupon applied. Discount: ₹${data.discountAmount}`);
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const placeOrder = async () => {
    setError('');
    try {
      const { data } = await api.post('/orders', { addressId, couponCode: couponCode || null, paymentMethod });
      navigate('/orders', { state: { createdOrder: data.orderNumber } });
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  if (loading) return <LoadingState label="Preparing checkout" />;
  if (error) return <ErrorState message={error} />;
  if (!cart?.items?.length) return <EmptyState title="Cart is empty" detail="Add items before checkout." />;

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">{cart.restaurantName}</p>
          <h1>Checkout</h1>
        </div>
      </div>
      {message && <div className="notice">{message}</div>}
      <div className="split-layout">
        <div className="panel-list">
          <section className="soft-panel">
            <h2>Delivery address</h2>
            {addresses.map((address) => (
              <label className="radio-card" key={address.id}>
                <input type="radio" checked={String(addressId) === String(address.id)} onChange={() => setAddressId(address.id)} />
                <span>{address.label}: {address.line1}, {address.city}</span>
              </label>
            ))}
            <form className="compact-form" onSubmit={saveAddress}>
              <input placeholder="Label" value={addressForm.label} onChange={(event) => setAddressForm({ ...addressForm, label: event.target.value })} />
              <input placeholder="Address line 1" value={addressForm.line1} onChange={(event) => setAddressForm({ ...addressForm, line1: event.target.value })} />
              <input placeholder="Address line 2" value={addressForm.line2} onChange={(event) => setAddressForm({ ...addressForm, line2: event.target.value })} />
              <div className="three-column">
                <input placeholder="City" value={addressForm.city} onChange={(event) => setAddressForm({ ...addressForm, city: event.target.value })} />
                <input placeholder="State" value={addressForm.state} onChange={(event) => setAddressForm({ ...addressForm, state: event.target.value })} />
                <input placeholder="Pincode" value={addressForm.pincode} onChange={(event) => setAddressForm({ ...addressForm, pincode: event.target.value })} />
              </div>
              <button className="secondary-button">Save address</button>
            </form>
          </section>
          <section className="soft-panel">
            <h2>Payment</h2>
            <select value={paymentMethod} onChange={(event) => setPaymentMethod(event.target.value)}>
              <option value="CASH_ON_DELIVERY">Cash on delivery</option>
              <option value="MOCK_UPI">Mock UPI</option>
              <option value="MOCK_CARD">Mock card</option>
            </select>
            <div className="coupon-row">
              <input placeholder="Coupon code" value={couponCode} onChange={(event) => setCouponCode(event.target.value)} />
              <button type="button" className="secondary-button" onClick={applyCoupon}>Apply</button>
            </div>
          </section>
        </div>
        <aside className="summary-panel">
          <h2>Payable</h2>
          <p><span>Subtotal</span><strong>₹{cart.subtotal}</strong></p>
          <p><span>Delivery fee</span><strong>₹{cart.deliveryFee}</strong></p>
          <p><span>Tax</span><strong>₹{cart.taxAmount}</strong></p>
          <p className="total"><span>Total</span><strong>₹{cart.totalAmount}</strong></p>
          <button className="primary-button full" disabled={!addressId} onClick={placeOrder}>Place order</button>
        </aside>
      </div>
    </section>
  );
}

