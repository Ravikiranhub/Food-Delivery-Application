# FoodHub Development Roadmap

This roadmap keeps the project interview-friendly and safe to build incrementally. Each stage should compile and preserve existing working behavior before moving to the next stage.

## Stage 1: Architecture and Design

Status: Complete

Deliverables:
- Project architecture
- Database ER design
- Entity relationship explanation
- API list
- Folder structure
- Development roadmap

Validation:
- Documentation is clear enough to explain in an interview.
- Backend, frontend, database, testing, and deployment boundaries are defined.

## Stage 2: Backend Project Skeleton

Deliverables:
- Maven Spring Boot project under `backend/`
- Java package structure under `com.foodhub`
- Spring Boot dependencies
- Environment-driven configuration
- Health-check endpoint
- Swagger/OpenAPI setup
- Global response/error structure foundation

Validation:
- Backend compiles.
- Health endpoint returns success.
- Swagger UI loads.
- No credentials are hardcoded.

## Stage 3: Core Domain Entities and Repositories

Deliverables:
- JPA entities for users, roles, restaurants, menu, cart, order, payment, review, favorite, delivery, and status history.
- Enum classes.
- Repository interfaces.
- Database constraints and indexes through JPA annotations and SQL schema.

Validation:
- Application starts against MySQL.
- Schema can be created successfully.
- Entity relationships are correct.

## Stage 4: Authentication and Authorization

Deliverables:
- Registration.
- Login.
- BCrypt password hashing.
- JWT generation and validation.
- Spring Security configuration.
- Role-based endpoint protection.
- Current-user endpoint.

Validation:
- Registration works.
- Login returns JWT.
- Protected APIs reject missing or invalid tokens.
- Role-restricted APIs reject unauthorized roles.

## Stage 5: Restaurant and Food Modules

Deliverables:
- Restaurant creation and management.
- Admin approval or rejection.
- Category management.
- Food item CRUD.
- Search and filters.
- Pagination.

Validation:
- Public users can browse approved restaurants and available food.
- Owners can manage only their own restaurants and menus.
- Admin can approve, reject, and suspend restaurants.

## Stage 6: Cart Module

Deliverables:
- Add item to cart.
- Update item quantity.
- Remove item.
- Clear cart.
- Enforce one-restaurant-per-cart rule.
- Coupon preview endpoint.

Validation:
- Cart totals are calculated on backend.
- Unavailable items cannot be added.
- Invalid quantities are rejected.

## Stage 7: Order Module

Deliverables:
- Place order from cart.
- Validate cart, restaurant, food items, address, coupon, and payment method.
- Calculate subtotal, tax, delivery fee, discount, and final amount.
- Create order items using item snapshots.
- Clear cart after successful order.
- Track status history.
- Cancel order when allowed.

Validation:
- Frontend cannot manipulate order total.
- Empty cart cannot create order.
- Invalid status transitions are rejected.
- Order history works for customer and owner views.

## Stage 8: Mock Payment Module

Deliverables:
- Mock payment record creation.
- Payment status handling.
- Supported methods: `CASH_ON_DELIVERY`, `MOCK_UPI`, `MOCK_CARD`.
- Payment lookup by order.

Validation:
- Order and payment amounts match.
- Mock failed payment does not incorrectly mark order paid.

## Stage 9: Reviews and Favorites

Deliverables:
- Restaurant reviews.
- Food item reviews.
- Rating aggregation.
- Favorite restaurants.

Validation:
- Only customers with delivered orders can review.
- Duplicate favorites are prevented.
- Public review pagination works.

## Stage 10: Delivery Partner Module

Deliverables:
- Delivery partner profile.
- Assigned orders.
- Accept delivery.
- Mark picked up.
- Mark delivered.
- Delivery history.
- Earnings summary.

Validation:
- Delivery partner can update only assigned deliveries.
- Delivery updates correctly affect order status.

## Stage 11: Admin Module and Reports

Deliverables:
- Admin dashboard.
- User management.
- Restaurant management.
- Delivery partner management.
- Order management.
- Coupon management.
- Platform statistics and reports.

Validation:
- Admin can block and unblock users.
- Admin statistics match database values.
- Admin APIs are inaccessible to non-admin roles.

## Stage 12: React Frontend Skeleton

Deliverables:
- Vite React project.
- Routing.
- Axios API client.
- Auth context.
- Protected routes.
- Role-specific layouts.
- Professional responsive styling foundation.

Validation:
- App runs locally.
- Login state persists across refresh.
- Unauthorized users are redirected appropriately.

## Stage 13: Customer Frontend

Deliverables:
- Home page.
- Restaurant list and filters.
- Restaurant details.
- Food browsing.
- Cart.
- Checkout.
- Order confirmation.
- Order tracking.
- Order history.
- Profile, addresses, and favorites.

Validation:
- Customer can complete a full demo order flow.
- Loading, error, and empty states are present.
- Mobile layout is usable.

## Stage 14: Restaurant Owner Frontend

Deliverables:
- Owner dashboard.
- Restaurant management.
- Food category management.
- Food item management.
- Incoming orders.
- Revenue and popular food views.

Validation:
- Owner can manage menu and process orders.
- Dashboard data is readable and responsive.

## Stage 15: Delivery Partner Frontend

Deliverables:
- Delivery dashboard.
- Assigned orders.
- Delivery status actions.
- Delivery history.
- Earnings.

Validation:
- Partner can accept and complete assigned delivery flow.

## Stage 16: Admin Frontend

Deliverables:
- Admin dashboard.
- User table.
- Restaurant approval table.
- Order table.
- Delivery partner table.
- Coupon management.
- Reports.

Validation:
- Admin workflows are protected and usable.
- Tables handle loading, empty, and error states.

## Stage 17: Testing

Deliverables:
- Unit tests for important service classes.
- Mockito-based business logic tests.
- Integration tests for key REST APIs.
- Authorization tests.

Required test coverage areas:
- Registration.
- Login.
- Restaurant creation.
- Food creation.
- Cart operations.
- Order creation.
- Order cancellation.
- Role-based authorization.

Validation:
- Backend test suite passes.
- Critical business rules have test coverage.

## Stage 18: Docker and Deployment

Deliverables:
- Backend Dockerfile.
- Frontend Dockerfile.
- Docker Compose with MySQL.
- `.env.example`.
- Production-ready configuration notes.

Validation:
- Full stack runs through Docker Compose.
- Credentials are supplied by environment variables.
- Backend and frontend can be deployed separately.

## Stage 19: README, Screenshots, and Resume Polish

Deliverables:
- Final README.
- Screenshots.
- API documentation links.
- Setup instructions.
- Resume bullet points.
- Future enhancements list.

Validation:
- A recruiter or interviewer can understand the project quickly.
- A developer can clone and run the project locally.
- Project demonstrates full-stack ownership.

## Suggested Future Enhancements

- Email notifications.
- Real payment gateway integration.
- Real-time order tracking with WebSocket.
- Restaurant availability schedule.
- Delivery partner geolocation.
- Recommendation system.
- Cloud deployment.
- CI/CD pipeline.

