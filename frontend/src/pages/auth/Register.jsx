import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { errorMessage } from '../../services/api.js';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    role: 'CUSTOMER',
    vehicleType: '',
    vehicleNumber: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      const user = await register(form);
      navigate(user.roles?.includes('RESTAURANT_OWNER') ? '/owner' : user.roles?.includes('DELIVERY_PARTNER') ? '/delivery' : '/');
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="auth-page">
      <form className="panel-form" onSubmit={submit}>
        <p className="eyebrow">Join FoodHub</p>
        <h1>Create account</h1>
        {error && <div className="form-error">{error}</div>}
        <input placeholder="Name" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
        <input type="email" placeholder="Email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
        <input placeholder="Phone" value={form.phone} onChange={(event) => setForm({ ...form, phone: event.target.value })} />
        <input type="password" placeholder="Password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} />
        <select value={form.role} onChange={(event) => setForm({ ...form, role: event.target.value })}>
          <option value="CUSTOMER">Customer</option>
          <option value="RESTAURANT_OWNER">Restaurant owner</option>
          <option value="DELIVERY_PARTNER">Delivery partner</option>
        </select>
        {form.role === 'DELIVERY_PARTNER' && (
          <div className="two-column">
            <input placeholder="Vehicle type" value={form.vehicleType} onChange={(event) => setForm({ ...form, vehicleType: event.target.value })} />
            <input placeholder="Vehicle number" value={form.vehicleNumber} onChange={(event) => setForm({ ...form, vehicleNumber: event.target.value })} />
          </div>
        )}
        <button className="primary-button" disabled={loading}>{loading ? 'Creating...' : 'Register'}</button>
        <p>Already registered? <Link to="/login">Login</Link></p>
      </form>
    </section>
  );
}

