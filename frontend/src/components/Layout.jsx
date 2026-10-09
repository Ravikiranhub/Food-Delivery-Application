import { Link, NavLink, Outlet } from 'react-router-dom';
import { Heart, LayoutDashboard, LogOut, PackageCheck, ShoppingCart, Store, Truck, UserRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext.jsx';

export default function Layout() {
  const { user, logout } = useAuth();

  return (
    <div className="app-shell">
      <header className="topbar">
        <Link to="/" className="brand">
          <span className="brand-mark">F</span>
          <span>FoodHub</span>
        </Link>
        <nav className="nav-links">
          <NavLink to="/"><Store size={18} /> Restaurants</NavLink>
          {user?.roles?.includes('CUSTOMER') && (
            <>
              <NavLink to="/cart"><ShoppingCart size={18} /> Cart</NavLink>
              <NavLink to="/orders"><PackageCheck size={18} /> Orders</NavLink>
              <NavLink to="/favorites"><Heart size={18} /> Favorites</NavLink>
              <NavLink to="/profile"><UserRound size={18} /> Profile</NavLink>
            </>
          )}
          {user?.roles?.includes('RESTAURANT_OWNER') && (
            <NavLink to="/owner"><LayoutDashboard size={18} /> Owner</NavLink>
          )}
          {user?.roles?.includes('DELIVERY_PARTNER') && (
            <NavLink to="/delivery"><Truck size={18} /> Delivery</NavLink>
          )}
          {user?.roles?.includes('ADMIN') && (
            <NavLink to="/admin"><LayoutDashboard size={18} /> Admin</NavLink>
          )}
        </nav>
        <div className="auth-actions">
          {user ? (
            <>
              <span className="user-chip">{user.name}</span>
              <button className="icon-button" onClick={logout} title="Logout">
                <LogOut size={18} />
              </button>
            </>
          ) : (
            <>
              <Link className="text-link" to="/login">Login</Link>
              <Link className="primary-link" to="/register">Register</Link>
            </>
          )}
        </div>
      </header>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}

