import { useEffect, useState } from 'react';
import { ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function AdminCoupons() {
  const [coupons, setCoupons] = useState([]);
  const [form, setForm] = useState({
    code: '',
    discountType: 'PERCENTAGE',
    discountValue: 10,
    minOrderAmount: 100,
    maxDiscountAmount: 100,
    validFrom: new Date().toISOString().slice(0, 16),
    validUntil: '',
    active: true
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const { data } = await api.get('/admin/coupons');
      setCoupons(data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const create = async (event) => {
    event.preventDefault();
    await api.post('/admin/coupons', {
      ...form,
      discountValue: Number(form.discountValue),
      minOrderAmount: Number(form.minOrderAmount),
      maxDiscountAmount: Number(form.maxDiscountAmount),
      validFrom: new Date(form.validFrom).toISOString(),
      validUntil: new Date(form.validUntil).toISOString()
    });
    setForm({ ...form, code: '' });
    load();
  };

  const setActive = async (coupon) => {
    await api.patch(`/admin/coupons/${coupon.id}/${coupon.active ? 'deactivate' : 'activate'}`);
    load();
  };

  if (loading) return <LoadingState label="Loading coupons" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <h1>Coupons</h1>
      <div className="split-layout">
        <section className="soft-panel">
          <h2>Existing coupons</h2>
          {coupons.map((coupon) => (
            <article className="line-item" key={coupon.id}>
              <div>
                <h3>{coupon.code}</h3>
                <p>{coupon.discountType} · {coupon.discountValue}</p>
              </div>
              <span className={coupon.active ? 'status-pill success' : 'status-pill'}>{coupon.active ? 'Active' : 'Inactive'}</span>
              <button className="secondary-button" onClick={() => setActive(coupon)}>{coupon.active ? 'Deactivate' : 'Activate'}</button>
            </article>
          ))}
        </section>
        <form className="soft-panel compact-form" onSubmit={create}>
          <h2>Create coupon</h2>
          <input placeholder="Code" value={form.code} onChange={(event) => setForm({ ...form, code: event.target.value })} />
          <select value={form.discountType} onChange={(event) => setForm({ ...form, discountType: event.target.value })}>
            <option value="PERCENTAGE">Percentage</option>
            <option value="FLAT">Flat</option>
          </select>
          <input type="number" value={form.discountValue} onChange={(event) => setForm({ ...form, discountValue: event.target.value })} />
          <input type="number" value={form.minOrderAmount} onChange={(event) => setForm({ ...form, minOrderAmount: event.target.value })} />
          <input type="number" value={form.maxDiscountAmount} onChange={(event) => setForm({ ...form, maxDiscountAmount: event.target.value })} />
          <input type="datetime-local" value={form.validFrom} onChange={(event) => setForm({ ...form, validFrom: event.target.value })} />
          <input type="datetime-local" value={form.validUntil} onChange={(event) => setForm({ ...form, validUntil: event.target.value })} />
          <button className="primary-button">Create coupon</button>
        </form>
      </div>
    </section>
  );
}

