import { useEffect, useState } from 'react';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [addresses, setAddresses] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const [profileRes, addressRes] = await Promise.all([api.get('/customers/profile'), api.get('/customers/addresses')]);
      setProfile(profileRes.data);
      setAddresses(addressRes.data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const save = async (event) => {
    event.preventDefault();
    const { data } = await api.put('/customers/profile', { name: profile.name, phone: profile.phone });
    setProfile(data);
  };

  if (loading) return <LoadingState label="Loading profile" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <h1>Profile</h1>
      <div className="split-layout">
        <form className="soft-panel compact-form" onSubmit={save}>
          <h2>Account</h2>
          <input value={profile.name} onChange={(event) => setProfile({ ...profile, name: event.target.value })} />
          <input value={profile.email} disabled />
          <input value={profile.phone} onChange={(event) => setProfile({ ...profile, phone: event.target.value })} />
          <button className="primary-button">Save profile</button>
        </form>
        <section className="soft-panel">
          <h2>Addresses</h2>
          {addresses.length ? addresses.map((address) => (
            <p key={address.id} className="address-line">{address.label}: {address.line1}, {address.city}</p>
          )) : <EmptyState title="No addresses" detail="Add one during checkout." />}
        </section>
      </div>
    </section>
  );
}

