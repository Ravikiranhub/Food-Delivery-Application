import { Search, Star } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { EmptyState, ErrorState, LoadingState } from '../components/State.jsx';
import api, { errorMessage, pageContent } from '../services/api.js';

export default function Home() {
  const [restaurants, setRestaurants] = useState([]);
  const [query, setQuery] = useState('');
  const [cuisine, setCuisine] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const timer = setTimeout(loadRestaurants, 250);
    return () => clearTimeout(timer);
  }, [query, cuisine]);

  const loadRestaurants = async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await api.get('/restaurants', {
        params: { keyword: query || undefined, cuisine: cuisine || undefined, open: true, size: 12 }
      });
      setRestaurants(pageContent(data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">Fresh near you</p>
          <h1>Restaurants accepting orders</h1>
        </div>
        <div className="search-row">
          <label className="input-with-icon">
            <Search size={18} />
            <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search restaurants" />
          </label>
          <input value={cuisine} onChange={(event) => setCuisine(event.target.value)} placeholder="Cuisine" />
        </div>
      </div>

      {loading && <LoadingState label="Finding restaurants" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && restaurants.length === 0 && (
        <EmptyState title="No restaurants found" detail="Try another cuisine or search term." />
      )}
      <div className="card-grid">
        {restaurants.map((restaurant) => (
          <Link className="restaurant-card" key={restaurant.id} to={`/restaurants/${restaurant.id}`}>
            <div className="restaurant-image" style={{ backgroundImage: `url(${restaurant.imageUrl || '/restaurant-placeholder.svg'})` }} />
            <div className="card-body">
              <div className="card-title-row">
                <h3>{restaurant.name}</h3>
                <span className="rating"><Star size={15} /> {restaurant.averageRating}</span>
              </div>
              <p>{restaurant.cuisineType} · {restaurant.city}</p>
              <span className="status-pill success">Open</span>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}

