# FoodHub Folder Structure

The project will use a backend/frontend split so each application can be developed, tested, containerized, and deployed independently.

```text
food-delivery/
|
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── foodhub/
│   │   │   │           ├── FoodHubApplication.java
│   │   │   │           ├── config/
│   │   │   │           ├── controller/
│   │   │   │           ├── dto/
│   │   │   │           │   ├── auth/
│   │   │   │           │   ├── cart/
│   │   │   │           │   ├── common/
│   │   │   │           │   ├── delivery/
│   │   │   │           │   ├── food/
│   │   │   │           │   ├── order/
│   │   │   │           │   ├── payment/
│   │   │   │           │   ├── restaurant/
│   │   │   │           │   ├── review/
│   │   │   │           │   └── user/
│   │   │   │           ├── entity/
│   │   │   │           ├── enums/
│   │   │   │           ├── exception/
│   │   │   │           ├── mapper/
│   │   │   │           ├── repository/
│   │   │   │           ├── security/
│   │   │   │           ├── service/
│   │   │   │           │   └── impl/
│   │   │   │           └── util/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       └── application-prod.properties
│   │   └── test/
│   │       └── java/
│   │           └── com/
│   │               └── foodhub/
│   │                   ├── controller/
│   │                   ├── integration/
│   │                   └── service/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   ├── dashboard/
│   │   │   ├── food/
│   │   │   ├── layout/
│   │   │   ├── order/
│   │   │   └── restaurant/
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── pages/
│   │   │   ├── admin/
│   │   │   ├── auth/
│   │   │   ├── customer/
│   │   │   ├── delivery/
│   │   │   └── owner/
│   │   ├── routes/
│   │   ├── services/
│   │   ├── styles/
│   │   ├── utils/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── Dockerfile
│   ├── package.json
│   └── vite.config.js
│
├── database/
│   ├── schema.sql
│   └── seed.sql
│
├── docs/
│   ├── architecture.md
│   ├── api-list.md
│   ├── database-design.md
│   ├── development-roadmap.md
│   └── folder-structure.md
│
├── screenshots/
│   └── .gitkeep
│
├── .env.example
├── .gitignore
├── docker-compose.yml
└── README.md
```

## Backend Package Responsibilities

`config/`
: Application configuration, CORS, OpenAPI, data seeding, and property binding.

`controller/`
: REST controllers grouped by domain.

`dto/`
: Request and response models used by APIs.

`entity/`
: JPA entities mapped to database tables.

`enums/`
: Role, order status, payment status, delivery status, restaurant status, and discount type enums.

`exception/`
: Custom exceptions and global exception handler.

`mapper/`
: Entity-to-DTO and DTO-to-entity mapping classes.

`repository/`
: Spring Data JPA repository interfaces.

`security/`
: JWT, Spring Security, password encoding, authentication filter, and user details service.

`service/`
: Business service interfaces.

`service/impl/`
: Business service implementations.

`util/`
: Small helper classes for calculations, order number generation, and constants.

## Frontend Folder Responsibilities

`components/common/`
: Shared buttons, inputs, modals, loaders, badges, empty states, and error states.

`components/layout/`
: Navbar, sidebar, dashboard shell, footer, and protected layout wrappers.

`components/restaurant/`
: Restaurant cards, filters, cuisine chips, restaurant summary panels.

`components/food/`
: Food cards, item details, quantity selector, availability badge.

`components/order/`
: Order summary, order timeline, order cards, status badges.

`components/dashboard/`
: Metric cards, charts, admin tables, owner summary widgets.

`context/`
: Authentication context, cart context, and optional UI state context.

`hooks/`
: Custom hooks for auth, API loading states, pagination, and form handling.

`pages/`
: Route-level pages grouped by role.

`routes/`
: Route definitions and protected route components.

`services/`
: Axios instance and domain API clients.

`styles/`
: Global CSS, layout CSS, responsive utilities, and component styles.

`utils/`
: Formatting helpers, constants, validators, and token utilities.

