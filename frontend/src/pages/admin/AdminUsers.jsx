import { useEffect, useState } from 'react';
import { ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage, pageContent } from '../../services/api.js';

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const { data } = await api.get('/admin/users', { params: { size: 100 } });
      setUsers(pageContent(data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const setActive = async (user) => {
    await api.patch(`/admin/users/${user.id}/${user.active ? 'block' : 'unblock'}`);
    load();
  };

  if (loading) return <LoadingState label="Loading users" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <h1>Users</h1>
      <DataTable
        headers={['Name', 'Email', 'Roles', 'Status', 'Action']}
        rows={users.map((user) => [
          user.name,
          user.email,
          user.roles.join(', '),
          user.active ? 'Active' : 'Blocked',
          <button className="secondary-button" onClick={() => setActive(user)}>{user.active ? 'Block' : 'Unblock'}</button>
        ])}
      />
    </section>
  );
}

function DataTable({ headers, rows }) {
  return (
    <div className="table-wrap">
      <table>
        <thead><tr>{headers.map((header) => <th key={header}>{header}</th>)}</tr></thead>
        <tbody>{rows.map((row, index) => <tr key={index}>{row.map((cell, cellIndex) => <td key={cellIndex}>{cell}</td>)}</tr>)}</tbody>
      </table>
    </div>
  );
}

