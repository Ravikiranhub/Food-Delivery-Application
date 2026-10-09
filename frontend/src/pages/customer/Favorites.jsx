import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage } from '../../services/api.js';

export default function Favorites() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/favorites')
      .then(({ data }) => setItems(data))
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingState label="Loading favorites" />;
  if (error) return <ErrorState message={error} />;
  if (!items.length) return <EmptyState title="No favorites yet" detail="Save restaurants you want to revisit." />;

  return (
    <section className="page-stack">
      <h1>Favorites</h1>
      <div className="card-grid">
        {items.map((restaurant) => (
          <Link className="restaurant-card" key={restaurant.id} to={`/restaurants/${restaurant.id}`}>
            <div className="restaurant-image" style={{ backgroundImage: `url(${restaurant.imageUrl || '/restaurant-placeholder.svg'})` }} />
            <div className="card-body">
              <h3>{restaurant.name}</h3>
              <p>{restaurant.cuisineType} · {restaurant.city}</p>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}

