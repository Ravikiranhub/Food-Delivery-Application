import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import AdminCoupons from './pages/admin/AdminCoupons.jsx';
import AdminDashboard from './pages/admin/AdminDashboard.jsx';
import AdminOrders from './pages/admin/AdminOrders.jsx';
import AdminRestaurants from './pages/admin/AdminRestaurants.jsx';
import AdminUsers from './pages/admin/AdminUsers.jsx';
import Login from './pages/auth/Login.jsx';
import Register from './pages/auth/Register.jsx';
import Cart from './pages/customer/Cart.jsx';
import Checkout from './pages/customer/Checkout.jsx';
import Favorites from './pages/customer/Favorites.jsx';
import Orders from './pages/customer/Orders.jsx';
import Profile from './pages/customer/Profile.jsx';
import DeliveryDashboard from './pages/delivery/DeliveryDashboard.jsx';
import Home from './pages/Home.jsx';
import OwnerDashboard from './pages/owner/OwnerDashboard.jsx';
import OwnerFood from './pages/owner/OwnerFood.jsx';
import OwnerOrders from './pages/owner/OwnerOrders.jsx';
import RestaurantDetails from './pages/RestaurantDetails.jsx';

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Home />} />
        <Route path="/restaurants/:id" element={<RestaurantDetails />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route element={<ProtectedRoute roles={['CUSTOMER']} />}>
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/favorites" element={<Favorites />} />
        </Route>

        <Route element={<ProtectedRoute roles={['RESTAURANT_OWNER']} />}>
          <Route path="/owner" element={<OwnerDashboard />} />
          <Route path="/owner/food" element={<OwnerFood />} />
          <Route path="/owner/orders" element={<OwnerOrders />} />
        </Route>

        <Route element={<ProtectedRoute roles={['DELIVERY_PARTNER']} />}>
          <Route path="/delivery" element={<DeliveryDashboard />} />
        </Route>

        <Route element={<ProtectedRoute roles={['ADMIN']} />}>
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/users" element={<AdminUsers />} />
          <Route path="/admin/restaurants" element={<AdminRestaurants />} />
          <Route path="/admin/orders" element={<AdminOrders />} />
          <Route path="/admin/coupons" element={<AdminCoupons />} />
        </Route>
      </Route>
    </Routes>
  );
}

