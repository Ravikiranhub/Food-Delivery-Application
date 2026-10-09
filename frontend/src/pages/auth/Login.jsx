import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { errorMessage } from '../../services/api.js';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    await doLogin(form);
  };

  const doLogin = async (payload) => {
    setLoading(true);
    setError('');
    try {
      const user = await login(payload);
      navigate(location.state?.from || homeFor(user), { replace: true });
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="auth-page">
      <form className="panel-form" onSubmit={submit}>
        <p className="eyebrow">Welcome back</p>
        <h1>Login</h1>
        {error && <div className="form-error">{error}</div>}
        <input type="email" placeholder="Email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
        <input type="password" placeholder="Password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} />
        <button className="primary-button" disabled={loading}>{loading ? 'Logging in...' : 'Login'}</button>
        <div className="demo-login-grid">
          <button type="button" className="secondary-button" onClick={() => doLogin({ email: 'customer@foodhub.local', password: 'Customer@12345' })}>Customer demo</button>
          <button type="button" className="secondary-button" onClick={() => doLogin({ email: 'owner@foodhub.local', password: 'Owner@12345' })}>Owner demo</button>
          <button type="button" className="secondary-button" onClick={() => doLogin({ email: 'delivery@foodhub.local', password: 'Delivery@12345' })}>Delivery demo</button>
          <button type="button" className="secondary-button" onClick={() => doLogin({ email: 'admin@foodhub.local', password: 'Admin@12345' })}>Admin demo</button>
        </div>
        <p>New to FoodHub? <Link to="/register">Create an account</Link></p>
      </form>
    </section>
  );
}

function homeFor(user) {
  if (user.roles?.includes('ADMIN')) return '/admin';
  if (user.roles?.includes('RESTAURANT_OWNER')) return '/owner';
  if (user.roles?.includes('DELIVERY_PARTNER')) return '/delivery';
  return '/';
}
