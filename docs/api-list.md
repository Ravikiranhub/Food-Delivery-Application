# FoodHub API List

Base path: `/api`

All protected APIs require:

```http
Authorization: Bearer <jwt_access_token>
```

## Authentication APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Public | Register a customer by default or a selected allowed role. |
| `POST` | `/auth/register/restaurant-owner` | Public | Register a restaurant owner. |
| `POST` | `/auth/register/delivery-partner` | Public | Register a delivery partner account. |
| `POST` | `/auth/login` | Public | Authenticate user and return JWT token. |
| `GET` | `/auth/me` | Authenticated | Return current authenticated user profile. |
| `POST` | `/auth/logout` | Authenticated | Client-side logout helper endpoint for consistency. |

## Customer APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/customers/dashboard` | Customer | Get customer dashboard summary. |
| `GET` | `/customers/profile` | Customer | Get own profile. |
| `PUT` | `/customers/profile` | Customer | Update own profile. |
| `GET` | `/customers/addresses` | Customer | List own addresses. |
| `POST` | `/customers/addresses` | Customer | Add delivery address. |
| `PUT` | `/customers/addresses/{id}` | Customer | Update own address. |
| `DELETE` | `/customers/addresses/{id}` | Customer | Delete own address. |
| `PUT` | `/customers/addresses/{id}/default` | Customer | Set default address. |

## Restaurant APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/restaurants` | Public | List approved restaurants with pagination and filters. |
| `GET` | `/restaurants/{id}` | Public | View restaurant details. |
| `GET` | `/restaurants/search` | Public | Search restaurants by keyword, cuisine, city, and open status. |
| `POST` | `/restaurants` | Restaurant owner | Create restaurant profile. |
| `PUT` | `/restaurants/{id}` | Restaurant owner | Update own restaurant information. |
| `DELETE` | `/restaurants/{id}` | Restaurant owner or admin | Soft-delete or deactivate restaurant. |
| `PATCH` | `/restaurants/{id}/open-status` | Restaurant owner | Open or close restaurant. |
| `GET` | `/restaurants/{id}/dashboard` | Restaurant owner | View restaurant dashboard metrics. |

Query parameters for list/search:
- `page`
- `size`
- `sort`
- `keyword`
- `cuisine`
- `city`
- `open`

## Food Category APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/restaurants/{restaurantId}/categories` | Public | List restaurant categories. |
| `POST` | `/restaurants/{restaurantId}/categories` | Restaurant owner | Create category. |
| `PUT` | `/categories/{id}` | Restaurant owner | Update category. |
| `DELETE` | `/categories/{id}` | Restaurant owner | Deactivate or delete category. |

## Food Item APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/food-items` | Public | List food items with pagination and filters. |
| `GET` | `/food-items/{id}` | Public | View food item details. |
| `GET` | `/restaurants/{restaurantId}/food-items` | Public | List food items for a restaurant. |
| `POST` | `/restaurants/{restaurantId}/food-items` | Restaurant owner | Add food item. |
| `PUT` | `/food-items/{id}` | Restaurant owner | Update food item. |
| `DELETE` | `/food-items/{id}` | Restaurant owner | Delete or deactivate food item. |
| `PATCH` | `/food-items/{id}/availability` | Restaurant owner | Set availability. |
| `PATCH` | `/food-items/{id}/price` | Restaurant owner | Update price. |

Query parameters:
- `page`
- `size`
- `sort`
- `restaurantId`
- `categoryId`
- `keyword`
- `vegetarian`
- `available`
- `minPrice`
- `maxPrice`

## Cart APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/cart` | Customer | Get current cart. |
| `POST` | `/cart/items` | Customer | Add item to cart. |
| `PUT` | `/cart/items/{id}` | Customer | Update item quantity. |
| `DELETE` | `/cart/items/{id}` | Customer | Remove item from cart. |
| `DELETE` | `/cart` | Customer | Clear cart. |
| `POST` | `/cart/apply-coupon` | Customer | Validate and preview coupon discount. |

## Order APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/orders` | Customer | Place order from cart. |
| `GET` | `/orders` | Authenticated | List orders for current role. |
| `GET` | `/orders/{id}` | Authorized user | View order details. |
| `POST` | `/orders/{id}/cancel` | Customer or admin | Cancel order when allowed. |
| `PUT` | `/orders/{id}/status` | Restaurant owner, delivery partner, or admin | Update order status. |
| `GET` | `/orders/{id}/status-history` | Authorized user | View status timeline. |

Order list filters:
- `page`
- `size`
- `status`
- `fromDate`
- `toDate`

## Payment APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/payments` | Customer | Create or confirm mock payment for an order. |
| `GET` | `/payments/{id}` | Authorized user | Get payment details. |
| `GET` | `/orders/{orderId}/payment` | Authorized user | Get payment for order. |

Payment methods:
- `CASH_ON_DELIVERY`
- `MOCK_UPI`
- `MOCK_CARD`

## Review APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/reviews` | Customer | Add restaurant or food review after delivery. |
| `GET` | `/restaurants/{id}/reviews` | Public | List restaurant reviews. |
| `GET` | `/food-items/{id}/reviews` | Public | List food item reviews. |
| `PUT` | `/reviews/{id}` | Customer | Update own review. |
| `DELETE` | `/reviews/{id}` | Customer or admin | Delete review. |

## Favorite APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/favorites` | Customer | List favorite restaurants. |
| `POST` | `/favorites/restaurants/{restaurantId}` | Customer | Add restaurant to favorites. |
| `DELETE` | `/favorites/restaurants/{restaurantId}` | Customer | Remove restaurant from favorites. |

## Restaurant Owner APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/owner/dashboard` | Restaurant owner | Owner-level dashboard summary. |
| `GET` | `/owner/restaurants` | Restaurant owner | List own restaurants. |
| `GET` | `/owner/orders` | Restaurant owner | View incoming orders for owned restaurants. |
| `POST` | `/owner/orders/{id}/accept` | Restaurant owner | Accept order. |
| `POST` | `/owner/orders/{id}/reject` | Restaurant owner | Reject order. |
| `PUT` | `/owner/orders/{id}/status` | Restaurant owner | Update preparation status. |
| `GET` | `/owner/revenue` | Restaurant owner | View revenue summary. |
| `GET` | `/owner/popular-foods` | Restaurant owner | View popular food items. |

## Delivery Partner APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/delivery/dashboard` | Delivery partner | Delivery dashboard. |
| `GET` | `/delivery/assigned-orders` | Delivery partner | View assigned orders. |
| `POST` | `/delivery/orders/{orderId}/accept` | Delivery partner | Accept assigned delivery. |
| `POST` | `/delivery/orders/{orderId}/picked-up` | Delivery partner | Mark order picked up. |
| `POST` | `/delivery/orders/{orderId}/delivered` | Delivery partner | Mark order delivered. |
| `GET` | `/delivery/history` | Delivery partner | View delivery history. |
| `GET` | `/delivery/earnings` | Delivery partner | View earnings. |
| `PATCH` | `/delivery/availability` | Delivery partner | Set availability. |

## Admin APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/admin/dashboard` | Admin | Platform dashboard. |
| `GET` | `/admin/users` | Admin | List users. |
| `PATCH` | `/admin/users/{id}/block` | Admin | Block user. |
| `PATCH` | `/admin/users/{id}/unblock` | Admin | Unblock user. |
| `GET` | `/admin/restaurants` | Admin | List restaurants by status. |
| `PATCH` | `/admin/restaurants/{id}/approve` | Admin | Approve restaurant. |
| `PATCH` | `/admin/restaurants/{id}/reject` | Admin | Reject restaurant. |
| `PATCH` | `/admin/restaurants/{id}/suspend` | Admin | Suspend restaurant. |
| `GET` | `/admin/orders` | Admin | List all orders. |
| `GET` | `/admin/delivery-partners` | Admin | Manage delivery partners. |
| `GET` | `/admin/reports` | Admin | View reports. |

## Coupon APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/admin/coupons` | Admin | List coupons. |
| `POST` | `/admin/coupons` | Admin | Create coupon. |
| `PUT` | `/admin/coupons/{id}` | Admin | Update coupon. |
| `PATCH` | `/admin/coupons/{id}/activate` | Admin | Activate coupon. |
| `PATCH` | `/admin/coupons/{id}/deactivate` | Admin | Deactivate coupon. |
| `DELETE` | `/admin/coupons/{id}` | Admin | Delete coupon. |

## Dashboard and Report APIs

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/dashboard/customer` | Customer | Customer order summary. |
| `GET` | `/dashboard/restaurant` | Restaurant owner | Restaurant metrics. |
| `GET` | `/dashboard/admin` | Admin | Platform statistics. |
| `GET` | `/reports/orders` | Admin | Order report. |
| `GET` | `/reports/revenue` | Admin | Revenue report. |
| `GET` | `/reports/restaurants` | Admin | Restaurant report. |

## Standard Error Response

All API errors will use a consistent JSON shape:

```json
{
  "timestamp": "2026-10-08T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/orders",
  "validationErrors": {
    "addressId": "Address is required"
  }
}
```

## Pagination Response Pattern

Paginated APIs will return:

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5,
  "last": false
}
```

