# FoodHub Architecture

## Architecture Style

FoodHub uses a clean layered architecture. Each layer has a clear responsibility and depends only on the layer below it.

```text
Client
  |
  v
React Frontend
  |
  v
REST API Controllers
  |
  v
DTOs and Validation
  |
  v
Service Layer
  |
  v
Repositories
  |
  v
JPA Entities
  |
  v
MySQL Database
```

## Backend Modules

### Controller Layer

Package: `com.foodhub.controller`

Responsibilities:
- Expose REST endpoints.
- Accept and validate request DTOs.
- Return response DTOs with proper HTTP status codes.
- Delegate business rules to services.
- Avoid direct repository access.

Example controller groups:
- `AuthController`
- `RestaurantController`
- `FoodItemController`
- `CartController`
- `OrderController`
- `PaymentController`
- `ReviewController`
- `CustomerController`
- `OwnerController`
- `DeliveryController`
- `AdminController`

### DTO Layer

Package: `com.foodhub.dto`

Responsibilities:
- Separate API payloads from JPA entities.
- Define request and response contracts.
- Apply Jakarta Bean Validation annotations.
- Prevent exposing sensitive fields such as password hashes.

DTO groups:
- `auth`
- `user`
- `restaurant`
- `food`
- `cart`
- `order`
- `payment`
- `review`
- `admin`
- `delivery`

### Service Layer

Package: `com.foodhub.service`

Responsibilities:
- Hold business rules.
- Manage transactions.
- Validate ownership and role-based actions.
- Calculate prices, tax, delivery fee, coupon discount, and final order total.
- Enforce allowed order status transitions.

Important services:
- `AuthService`
- `UserService`
- `RestaurantService`
- `FoodCategoryService`
- `FoodItemService`
- `CartService`
- `OrderService`
- `PaymentService`
- `CouponService`
- `ReviewService`
- `FavoriteService`
- `DeliveryService`
- `AdminService`
- `DashboardService`

### Repository Layer

Package: `com.foodhub.repository`

Responsibilities:
- Use Spring Data JPA repositories.
- Provide query methods for common filters.
- Use pagination for large lists.
- Avoid business logic.

Common query requirements:
- Restaurants by cuisine, approval status, active status, owner, city, and keyword.
- Food items by restaurant, category, availability, and keyword.
- Orders by customer, restaurant, delivery partner, status, and date range.
- Reviews by restaurant and food item.

### Entity Layer

Package: `com.foodhub.entity`

Responsibilities:
- Represent normalized database tables.
- Define JPA relationships.
- Define database constraints and indexes.
- Avoid API-specific fields.

### Mapper Layer

Package: `com.foodhub.mapper`

Responsibilities:
- Convert entities to response DTOs.
- Convert request DTOs into entities where appropriate.
- Keep mapping logic out of controllers and services.

Manual mapper classes are preferred at first for interview clarity. MapStruct can be introduced later if mapping grows large.

### Exception Layer

Package: `com.foodhub.exception`

Responsibilities:
- Define domain-specific exceptions.
- Convert exceptions into consistent JSON error responses with `@RestControllerAdvice`.

Planned exceptions:
- `ResourceNotFoundException`
- `InvalidOrderException`
- `UnauthorizedException`
- `RestaurantNotAvailableException`
- `InsufficientStockException`
- `CouponNotApplicableException`
- `InvalidOrderStatusTransitionException`

### Security Layer

Package: `com.foodhub.security`

Responsibilities:
- Authenticate users with email and password.
- Hash passwords using BCrypt.
- Generate and validate JWT access tokens.
- Load authenticated user details.
- Enforce role-based endpoint protection.
- Configure stateless Spring Security.

Security components:
- `JwtService`
- `JwtAuthenticationFilter`
- `CustomUserDetailsService`
- `SecurityConfig`
- `AuthenticationEntryPoint`
- `AccessDeniedHandler`

### Configuration Layer

Package: `com.foodhub.config`

Responsibilities:
- CORS configuration.
- Swagger/OpenAPI configuration.
- Application property binding.
- Initial seed data setup for roles and optional admin user.

## Frontend Architecture

The React frontend will use route-based pages, reusable UI components, service modules for API calls, and context for authentication and cart state.

```text
React App
  |
  v
Routes and Protected Routes
  |
  v
Pages
  |
  v
Reusable Components
  |
  v
Axios API Services
  |
  v
Spring Boot REST API
```

Frontend responsibilities:
- JWT storage and logout flow.
- Protected routes by role.
- Customer browsing and checkout.
- Owner dashboard and menu management.
- Delivery dashboard.
- Admin management pages.
- Loading, error, and empty states.

## Authentication Flow

```text
User submits login/register form
  |
  v
Backend validates credentials
  |
  v
Password is verified with BCrypt
  |
  v
JWT access token is generated
  |
  v
Frontend stores token
  |
  v
Axios sends token in Authorization header
  |
  v
Backend JWT filter authenticates protected requests
```

The application will use stateless JWT authentication. Tokens will contain user identity and role claims. Passwords will never be stored or returned in plain text.

## Order Placement Flow

```text
Customer places order
  |
  v
Validate cart
  |
  v
Validate restaurant availability
  |
  v
Validate food item availability and quantity
  |
  v
Calculate subtotal on backend
  |
  v
Apply coupon if valid
  |
  v
Calculate delivery fee and tax
  |
  v
Create order and order items
  |
  v
Create mock payment record
  |
  v
Clear cart
  |
  v
Create order status history entry
```

Order totals will always be calculated on the backend. The frontend may display estimated totals, but cannot control final payable amount.

## Order Status Workflow

```text
PLACED
  |
  v
CONFIRMED
  |
  v
PREPARING
  |
  v
READY_FOR_PICKUP
  |
  v
OUT_FOR_DELIVERY
  |
  v
DELIVERED
```

Terminal or alternate states:
- `CANCELLED`
- `REJECTED`

Status rules:
- A customer can cancel only while the order is in an allowed early state.
- A restaurant owner can accept, reject, and move preparation statuses for their own restaurant orders.
- A delivery partner can update pickup and delivery statuses only for assigned or accepted deliveries.
- An admin can view and manage all orders.

## Authorization Model

Access will be controlled at both API and UI levels.

Backend:
- `CUSTOMER`: cart, checkout, reviews, favorites, own profile, own orders.
- `RESTAURANT_OWNER`: own restaurants, menus, incoming restaurant orders, owner dashboard.
- `DELIVERY_PARTNER`: assigned deliveries, delivery history, earnings.
- `ADMIN`: user, restaurant, order, coupon, category, report, and platform management.

Frontend:
- Protected route components.
- Role-specific layouts and navigation.
- Redirect unauthorized users to a safe dashboard or login page.

## Configuration Strategy

All sensitive or environment-specific values will come from environment variables.

Planned backend variables:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `CORS_ALLOWED_ORIGINS`
- `APP_ADMIN_EMAIL`
- `APP_ADMIN_PASSWORD`

Planned frontend variables:
- `VITE_API_BASE_URL`

## API Documentation

Swagger/OpenAPI will be available from the backend during development.

Planned local URLs:
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Frontend: `http://localhost:5173`

