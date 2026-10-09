# FoodHub - Full Stack Food Delivery Application

FoodHub is a production-style full-stack food delivery platform built with Spring Boot, MySQL, JWT authentication, and React. It is designed as a major Java Full Stack fresher resume project with real marketplace workflows for customers, restaurant owners, delivery partners, and admins.

## Features

- JWT registration and login with BCrypt password hashing.
- Role-based authorization for `CUSTOMER`, `RESTAURANT_OWNER`, `DELIVERY_PARTNER`, and `ADMIN`.
- Restaurant discovery with search, cuisine filter, open status, and pagination.
- Restaurant owner restaurant profile and menu management.
- Food categories and food item management.
- Cart with quantity updates, removal, clear cart, coupon preview, and one-restaurant cart rule.
- Backend-owned checkout calculation for subtotal, discount, delivery fee, tax, and final total.
- Mock payment support for `CASH_ON_DELIVERY`, `MOCK_UPI`, and `MOCK_CARD`.
- Order status workflow from `PLACED` to `DELIVERED`, plus `CANCELLED` and `REJECTED`.
- Order status history for tracking.
- Reviews, ratings, and favorite restaurants.
- Delivery partner pickup, delivery, history, availability, and earnings.
- Admin dashboard, users, restaurants, orders, coupons, and reports.
- Swagger/OpenAPI documentation.
- React frontend with protected routes, role-aware navigation, dashboards, loading states, error states, and responsive design.
- Docker Compose setup for MySQL, backend, and frontend.

## Technology Stack

Backend:
- Java 17
- Spring Boot 3.3
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Jakarta Bean Validation
- Lombok
- JUnit 5
- Mockito
- Swagger/OpenAPI

Frontend:
- React
- JavaScript
- React Router
- Axios
- HTML5
- CSS3
- Vite

Deployment:
- Docker
- Docker Compose
- Environment variables

## Project Structure

```text
food-delivery/
├── backend/
│   ├── src/main/java/com/foodhub/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   ├── src/main/resources/
│   ├── src/test/java/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── services/
│   │   └── styles/
│   ├── Dockerfile
│   └── package.json
├── database/
│   ├── schema.sql
│   └── seed.sql
├── docs/
├── screenshots/
├── .env.example
└── docker-compose.yml
```

## Environment Variables

Create a `.env` file from `.env.example`.

Important backend variables:

```env
DB_URL=jdbc:mysql://localhost:3306/foodhub?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=foodhub
DB_PASSWORD=change_me
JWT_SECRET=replace_with_at_least_32_characters_secret_value
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:5173
APP_ADMIN_EMAIL=admin@foodhub.local
APP_ADMIN_PASSWORD=ChangeMe@12345
```

Important frontend variable:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Do not commit real passwords, database credentials, JWT secrets, or API keys.

## Run With Docker

From the project root:

```bash
docker compose --env-file .env up --build
```

URLs:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Run Backend Locally

Start MySQL first, then run:

```bash
cd backend
mvn spring-boot:run
```

Run tests:

```bash
cd backend
mvn test
```

## Run Frontend Locally

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

## Demo Flow

1. Start MySQL and backend.
2. Start frontend.
3. Register a restaurant owner.
4. Create a restaurant.
5. Login as admin and approve the restaurant.
6. Login as restaurant owner and open the restaurant.
7. Add categories and food items.
8. Register a customer.
9. Browse restaurant, add food to cart, add address, and place an order.
10. Restaurant owner accepts and prepares the order.
11. Delivery partner accepts pickup and marks the order delivered.

## API Documentation

Swagger is configured at:

```text
http://localhost:8080/swagger-ui.html
```

Main API groups:
- Authentication
- Customers
- Restaurants
- Food categories
- Food items
- Cart
- Orders
- Payments
- Reviews
- Favorites
- Restaurant owner
- Delivery partner
- Admin
- Coupons

## Database Design

The normalized MySQL schema includes:

- `users`
- `roles`
- `user_roles`
- `addresses`
- `restaurants`
- `food_categories`
- `food_items`
- `carts`
- `cart_items`
- `coupons`
- `orders`
- `order_items`
- `payments`
- `reviews`
- `favorites`
- `delivery_partners`
- `deliveries`
- `order_status_history`

See [docs/database-design.md](docs/database-design.md) for the ER diagram and relationship explanation.

## Testing

Included backend tests cover:
- Registration
- Login
- Protected endpoint rejection
- Cart business rule for unavailable food items

More integration tests can be added for order placement, restaurant approval, authorization, and delivery workflow.

## Documentation

- [Architecture](docs/architecture.md)
- [Database Design](docs/database-design.md)
- [API List](docs/api-list.md)
- [Folder Structure](docs/folder-structure.md)
- [Development Roadmap](docs/development-roadmap.md)

## Resume Highlights

- Built secure JWT authentication and role-based authorization using Spring Security.
- Designed normalized MySQL schema with JPA relationships and indexes.
- Implemented backend-owned order pricing to prevent frontend total manipulation.
- Built full order workflow with restaurant, customer, delivery, and admin roles.
- Created responsive React frontend with protected routes and dashboard views.
- Containerized backend, frontend, and MySQL using Docker Compose.

## Future Enhancements

- Refresh tokens.
- Email/SMS notifications.
- Real payment gateway integration.
- WebSocket order status updates.
- Delivery partner live location tracking.
- CI/CD pipeline.
- Cloud deployment.
