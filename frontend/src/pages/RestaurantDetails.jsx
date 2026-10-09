import { Heart, Plus, Star } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { EmptyState, ErrorState, LoadingState } from '../components/State.jsx';
import api, { errorMessage, pageContent } from '../services/api.js';

export default function RestaurantDetails() {
  const { id } = useParams();
  const { user } = useAuth();
  const [restaurant, setRestaurant] = useState(null);
  const [foods, setFoods] = useState([]);
  const [categories, setCategories] = useState([]);
  const [categoryId, setCategoryId] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, [id, categoryId]);

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const [restaurantRes, categoryRes, foodRes] = await Promise.all([
        api.get(`/restaurants/${id}`),
        api.get(`/restaurants/${id}/categories`),
        api.get(`/restaurants/${id}/food-items`, { params: { categoryId: categoryId || undefined, size: 60 } })
      ]);
      setRestaurant(restaurantRes.data);
      setCategories(categoryRes.data);
      setFoods(pageContent(foodRes.data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const addToCart = async (foodItemId) => {
    setMessage('');
    try {
      await api.post('/cart/items', { foodItemId, quantity: 1 });
      setMessage('Added to cart');
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const favorite = async () => {
    try {
      await api.post(`/favorites/restaurants/${id}`);
      setMessage('Added to favorites');
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  if (loading) return <LoadingState label="Loading restaurant" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="page-stack">
      <div className="detail-hero">
        <div className="detail-image" style={{ backgroundImage: `url(${restaurant.imageUrl || '/restaurant-placeholder.svg'})` }} />
        <div>
          <p className="eyebrow">{restaurant.cuisineType}</p>
          <h1>{restaurant.name}</h1>
          <p>{restaurant.description || 'Curated meals, prepared fresh for delivery.'}</p>
          <div className="inline-meta">
            <span><Star size={16} /> {restaurant.averageRating}</span>
            <span>{restaurant.city}</span>
            <span className={restaurant.open ? 'status-pill success' : 'status-pill'}>{restaurant.open ? 'Open' : 'Closed'}</span>
          </div>
          {user?.roles?.includes('CUSTOMER') && (
            <button className="secondary-button" onClick={favorite}><Heart size={18} /> Favorite</button>
          )}
        </div>
      </div>

      {message && <div className="notice">{message}</div>}

      <div className="segment-row">
        <button className={!categoryId ? 'segment active' : 'segment'} onClick={() => setCategoryId('')}>All</button>
        {categories.map((category) => (
          <button
            key={category.id}
            className={String(category.id) === String(categoryId) ? 'segment active' : 'segment'}
            onClick={() => setCategoryId(category.id)}
          >
            {category.name}
          </button>
        ))}
      </div>

      {foods.length === 0 ? (
        <EmptyState title="No food items" detail="This restaurant has not listed available food yet." />
      ) : (
        <div className="food-grid">
          {foods.map((food) => (
            <article className="food-card" key={food.id}>
              <div className="food-image" style={{ backgroundImage: `url(${food.imageUrl || '/food-placeholder.svg'})` }} />
              <div>
                <div className="card-title-row">
                  <h3>{food.name}</h3>
                  <strong>₹{food.price}</strong>
                </div>
                <p>{food.description || food.categoryName}</p>
                <div className="inline-meta">
                  <span>{food.vegetarian ? 'Vegetarian' : 'Non vegetarian'}</span>
                  <span>{food.preparationTimeMinutes} min</span>
                </div>
              </div>
              {user?.roles?.includes('CUSTOMER') && (
                <button className="icon-text-button" onClick={() => addToCart(food.id)}>
                  <Plus size={18} /> Add
                </button>
              )}
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

