import { useEffect, useState } from 'react';
import { EmptyState, ErrorState, LoadingState } from '../../components/State.jsx';
import api, { errorMessage, pageContent } from '../../services/api.js';

export default function OwnerFood() {
  const [restaurants, setRestaurants] = useState([]);
  const [restaurantId, setRestaurantId] = useState('');
  const [categories, setCategories] = useState([]);
  const [foods, setFoods] = useState([]);
  const [categoryName, setCategoryName] = useState('');
  const [foodForm, setFoodForm] = useState({
    categoryId: '',
    name: '',
    description: '',
    price: '',
    vegetarian: true,
    available: true,
    imageUrl: '',
    preparationTimeMinutes: 20
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/owner/restaurants')
      .then(({ data }) => {
        setRestaurants(data);
        setRestaurantId(data[0]?.id || '');
      })
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (restaurantId) loadMenu();
  }, [restaurantId]);

  const loadMenu = async () => {
    const [categoryRes, foodRes] = await Promise.all([
      api.get(`/restaurants/${restaurantId}/categories`),
      api.get('/food-items', { params: { restaurantId, size: 100 } })
    ]);
    setCategories(categoryRes.data);
    setFoods(pageContent(foodRes.data));
    setFoodForm((current) => ({ ...current, categoryId: categoryRes.data[0]?.id || '' }));
  };

  const addCategory = async (event) => {
    event.preventDefault();
    await api.post(`/restaurants/${restaurantId}/categories`, { name: categoryName });
    setCategoryName('');
    loadMenu();
  };

  const addFood = async (event) => {
    event.preventDefault();
    await api.post(`/restaurants/${restaurantId}/food-items`, {
      ...foodForm,
      price: Number(foodForm.price),
      preparationTimeMinutes: Number(foodForm.preparationTimeMinutes)
    });
    setFoodForm({ ...foodForm, name: '', description: '', price: '', imageUrl: '' });
    loadMenu();
  };

  const setAvailability = async (food) => {
    await api.patch(`/food-items/${food.id}/availability`, null, { params: { available: !food.available } });
    loadMenu();
  };

  if (loading) return <LoadingState label="Loading menu manager" />;
  if (error) return <ErrorState message={error} />;
  if (!restaurants.length) return <EmptyState title="Create a restaurant first" detail="Menu management starts after a restaurant profile exists." />;

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">Restaurant owner</p>
          <h1>Food management</h1>
        </div>
        <select value={restaurantId} onChange={(event) => setRestaurantId(event.target.value)}>
          {restaurants.map((restaurant) => <option key={restaurant.id} value={restaurant.id}>{restaurant.name}</option>)}
        </select>
      </div>
      <div className="split-layout">
        <section className="soft-panel">
          <h2>Menu items</h2>
          {!foods.length ? <EmptyState title="No food items" detail="Add categories and food to publish your menu." /> : foods.map((food) => (
            <article className="line-item" key={food.id}>
              <div className="food-thumb" style={{ backgroundImage: `url(${food.imageUrl || '/food-placeholder.svg'})` }} />
              <div>
                <h3>{food.name}</h3>
                <p>₹{food.price} · {food.categoryName}</p>
              </div>
              <span className={food.available ? 'status-pill success' : 'status-pill'}>{food.available ? 'Available' : 'Hidden'}</span>
              <button className="secondary-button" onClick={() => setAvailability(food)}>{food.available ? 'Hide' : 'Show'}</button>
            </article>
          ))}
        </section>
        <div className="panel-list">
          <form className="soft-panel compact-form" onSubmit={addCategory}>
            <h2>Add category</h2>
            <input placeholder="Category name" value={categoryName} onChange={(event) => setCategoryName(event.target.value)} />
            <button className="secondary-button">Add category</button>
          </form>
          <form className="soft-panel compact-form" onSubmit={addFood}>
            <h2>Add food</h2>
            <select value={foodForm.categoryId} onChange={(event) => setFoodForm({ ...foodForm, categoryId: event.target.value })}>
              {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
            </select>
            <input placeholder="Name" value={foodForm.name} onChange={(event) => setFoodForm({ ...foodForm, name: event.target.value })} />
            <input placeholder="Price" type="number" value={foodForm.price} onChange={(event) => setFoodForm({ ...foodForm, price: event.target.value })} />
            <input placeholder="Image URL" value={foodForm.imageUrl} onChange={(event) => setFoodForm({ ...foodForm, imageUrl: event.target.value })} />
            <textarea placeholder="Description" value={foodForm.description} onChange={(event) => setFoodForm({ ...foodForm, description: event.target.value })} />
            <div className="two-column">
              <label className="checkbox-row"><input type="checkbox" checked={foodForm.vegetarian} onChange={(event) => setFoodForm({ ...foodForm, vegetarian: event.target.checked })} /> Vegetarian</label>
              <input type="number" value={foodForm.preparationTimeMinutes} onChange={(event) => setFoodForm({ ...foodForm, preparationTimeMinutes: event.target.value })} />
            </div>
            <button className="primary-button">Add food</button>
          </form>
        </div>
      </div>
    </section>
  );
}

