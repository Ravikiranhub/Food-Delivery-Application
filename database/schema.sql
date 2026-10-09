CREATE DATABASE IF NOT EXISTS foodhub;
USE foodhub;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  phone VARCHAR(20) NOT NULL UNIQUE,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  INDEX idx_users_email (email),
  INDEX idx_users_phone (phone)
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS addresses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  label VARCHAR(80) NOT NULL,
  line1 VARCHAR(220) NOT NULL,
  line2 VARCHAR(220),
  city VARCHAR(80) NOT NULL,
  state VARCHAR(80) NOT NULL,
  pincode VARCHAR(12) NOT NULL,
  latitude DECIMAL(10,7),
  longitude DECIMAL(10,7),
  is_default BOOLEAN NOT NULL DEFAULT FALSE,
  INDEX idx_addresses_user_id (user_id),
  CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS restaurants (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_id BIGINT NOT NULL,
  name VARCHAR(140) NOT NULL,
  description VARCHAR(800),
  cuisine_type VARCHAR(80) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  email VARCHAR(160),
  image_url VARCHAR(600),
  status VARCHAR(40) NOT NULL,
  is_open BOOLEAN NOT NULL DEFAULT FALSE,
  average_rating DECIMAL(3,2) NOT NULL DEFAULT 0,
  city VARCHAR(80) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_owner_restaurant_name (owner_id, name),
  INDEX idx_restaurants_city (city),
  INDEX idx_restaurants_cuisine_type (cuisine_type),
  INDEX idx_restaurants_status (status),
  INDEX idx_restaurants_open (is_open),
  CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS food_categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  restaurant_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  UNIQUE KEY uk_restaurant_category_name (restaurant_id, name),
  INDEX idx_food_categories_restaurant_id (restaurant_id),
  CONSTRAINT fk_categories_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE IF NOT EXISTS food_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  restaurant_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  name VARCHAR(140) NOT NULL,
  description VARCHAR(800),
  price DECIMAL(10,2) NOT NULL,
  vegetarian BOOLEAN NOT NULL DEFAULT TRUE,
  available BOOLEAN NOT NULL DEFAULT TRUE,
  image_url VARCHAR(600),
  preparation_time_minutes INT NOT NULL,
  average_rating DECIMAL(3,2) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  INDEX idx_food_items_restaurant_id (restaurant_id),
  INDEX idx_food_items_category_id (category_id),
  INDEX idx_food_items_available (available),
  CONSTRAINT fk_food_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
  CONSTRAINT fk_food_items_category FOREIGN KEY (category_id) REFERENCES food_categories(id)
);

CREATE TABLE IF NOT EXISTS carts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL UNIQUE,
  restaurant_id BIGINT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  INDEX idx_carts_customer_id (customer_id),
  CONSTRAINT fk_carts_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_carts_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  cart_id BIGINT NOT NULL,
  food_item_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit_price_snapshot DECIMAL(10,2) NOT NULL,
  UNIQUE KEY uk_cart_food_item (cart_id, food_item_id),
  INDEX idx_cart_items_cart_id (cart_id),
  CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
  CONSTRAINT fk_cart_items_food FOREIGN KEY (food_item_id) REFERENCES food_items(id)
);

CREATE TABLE IF NOT EXISTS coupons (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(40) NOT NULL UNIQUE,
  discount_type VARCHAR(40) NOT NULL,
  discount_value DECIMAL(10,2) NOT NULL,
  min_order_amount DECIMAL(10,2) NOT NULL,
  max_discount_amount DECIMAL(10,2),
  valid_from DATETIME NOT NULL,
  valid_until DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_number VARCHAR(40) NOT NULL UNIQUE,
  customer_id BIGINT NOT NULL,
  restaurant_id BIGINT NOT NULL,
  delivery_address_id BIGINT NOT NULL,
  coupon_id BIGINT,
  status VARCHAR(40) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  discount_amount DECIMAL(10,2) NOT NULL,
  delivery_fee DECIMAL(10,2) NOT NULL,
  tax_amount DECIMAL(10,2) NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  payment_method VARCHAR(40) NOT NULL,
  placed_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  INDEX idx_orders_customer_id (customer_id),
  INDEX idx_orders_restaurant_id (restaurant_id),
  INDEX idx_orders_status (status),
  INDEX idx_orders_placed_at (placed_at),
  CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
  CONSTRAINT fk_orders_address FOREIGN KEY (delivery_address_id) REFERENCES addresses(id),
  CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  food_item_id BIGINT NOT NULL,
  item_name_snapshot VARCHAR(160) NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL,
  item_total DECIMAL(10,2) NOT NULL,
  INDEX idx_order_items_order_id (order_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_items_food FOREIGN KEY (food_item_id) REFERENCES food_items(id)
);

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL UNIQUE,
  payment_method VARCHAR(40) NOT NULL,
  payment_status VARCHAR(40) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  transaction_reference VARCHAR(120),
  paid_at DATETIME,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS reviews (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  restaurant_id BIGINT NOT NULL,
  food_item_id BIGINT,
  order_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comment VARCHAR(800),
  created_at DATETIME NOT NULL,
  INDEX idx_reviews_restaurant_id (restaurant_id),
  INDEX idx_reviews_food_item_id (food_item_id),
  CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_reviews_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
  CONSTRAINT fk_reviews_food FOREIGN KEY (food_item_id) REFERENCES food_items(id),
  CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  restaurant_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  UNIQUE KEY uk_customer_restaurant_favorite (customer_id, restaurant_id),
  INDEX idx_favorites_customer_id (customer_id),
  CONSTRAINT fk_favorites_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_favorites_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE IF NOT EXISTS delivery_partners (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  vehicle_type VARCHAR(60),
  vehicle_number VARCHAR(40),
  available BOOLEAN NOT NULL DEFAULT TRUE,
  total_earnings DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_delivery_partners_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS deliveries (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL UNIQUE,
  delivery_partner_id BIGINT,
  status VARCHAR(40) NOT NULL,
  delivery_fee DECIMAL(10,2) NOT NULL,
  assigned_at DATETIME,
  accepted_at DATETIME,
  picked_up_at DATETIME,
  delivered_at DATETIME,
  INDEX idx_deliveries_partner_id (delivery_partner_id),
  INDEX idx_deliveries_status (status),
  CONSTRAINT fk_deliveries_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_deliveries_partner FOREIGN KEY (delivery_partner_id) REFERENCES delivery_partners(id)
);

CREATE TABLE IF NOT EXISTS order_status_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  status VARCHAR(40) NOT NULL,
  changed_by_user_id BIGINT,
  note VARCHAR(500),
  changed_at DATETIME NOT NULL,
  INDEX idx_order_status_history_order_id (order_id),
  CONSTRAINT fk_history_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id)
);

