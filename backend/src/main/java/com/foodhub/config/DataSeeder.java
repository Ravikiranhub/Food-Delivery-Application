package com.foodhub.config;

import com.foodhub.entity.Role;
import com.foodhub.entity.Coupon;
import com.foodhub.entity.FoodCategory;
import com.foodhub.entity.FoodItem;
import com.foodhub.entity.Restaurant;
import com.foodhub.entity.User;
import com.foodhub.enums.DiscountType;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.enums.RoleName;
import com.foodhub.repository.CouponRepository;
import com.foodhub.repository.DeliveryPartnerRepository;
import com.foodhub.repository.FoodCategoryRepository;
import com.foodhub.repository.FoodItemRepository;
import com.foodhub.repository.RestaurantRepository;
import com.foodhub.repository.RoleRepository;
import com.foodhub.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodItemRepository foodItemRepository;
    private final CouponRepository couponRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.demo.seed:false}")
    private boolean demoSeed;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
            }

            if (StringUtils.hasText(adminEmail) && StringUtils.hasText(adminPassword)
                    && !userRepository.existsByEmail(adminEmail)) {
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseThrow();
                User admin = User.builder()
                        .name("FoodHub Admin")
                        .email(adminEmail)
                        .phone("9999999999")
                        .passwordHash(passwordEncoder.encode(adminPassword))
                        .roles(Set.of(adminRole))
                        .active(true)
                        .build();
                userRepository.save(admin);
            }

            if (demoSeed && restaurantRepository.count() == 0) {
                seedDemoData();
            }
        };
    }

    private void seedDemoData() {
        Role ownerRole = roleRepository.findByName(RoleName.RESTAURANT_OWNER).orElseThrow();
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER).orElseThrow();
        Role deliveryRole = roleRepository.findByName(RoleName.DELIVERY_PARTNER).orElseThrow();

        User owner = userRepository.findByEmail("owner@foodhub.local")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Demo Restaurant Owner")
                        .email("owner@foodhub.local")
                        .phone("9000000001")
                        .passwordHash(passwordEncoder.encode("Owner@12345"))
                        .roles(Set.of(ownerRole))
                        .active(true)
                        .build()));

        userRepository.findByEmail("customer@foodhub.local")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Demo Customer")
                        .email("customer@foodhub.local")
                        .phone("9000000002")
                        .passwordHash(passwordEncoder.encode("Customer@12345"))
                        .roles(Set.of(customerRole))
                        .active(true)
                        .build()));

        User deliveryUser = userRepository.findByEmail("delivery@foodhub.local")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Demo Delivery Partner")
                        .email("delivery@foodhub.local")
                        .phone("9000000003")
                        .passwordHash(passwordEncoder.encode("Delivery@12345"))
                        .roles(Set.of(deliveryRole))
                        .active(true)
                        .build()));

        deliveryPartnerRepository.findByUserId(deliveryUser.getId())
                .orElseGet(() -> deliveryPartnerRepository.save(com.foodhub.entity.DeliveryPartner.builder()
                        .user(deliveryUser)
                        .vehicleType("Bike")
                        .vehicleNumber("FH-2026")
                        .available(true)
                        .totalEarnings(BigDecimal.ZERO)
                        .build()));

        Restaurant spiceGarden = createRestaurant(owner, "Spice Garden", "North Indian comfort meals, biryanis, and rich curries.", "North Indian", "Bengaluru");
        addMenu(spiceGarden, "Biryani", new Object[][]{
                {"Hyderabadi Veg Biryani", "Aromatic basmati rice with vegetables and saffron.", "220.00", true, 28},
                {"Chicken Dum Biryani", "Slow-cooked chicken biryani with house spices.", "280.00", false, 32}
        });
        addMenu(spiceGarden, "Curries", new Object[][]{
                {"Paneer Butter Masala", "Creamy tomato gravy with paneer cubes.", "240.00", true, 22},
                {"Dal Tadka", "Yellow lentils tempered with garlic and cumin.", "170.00", true, 18}
        });

        Restaurant urbanTiffin = createRestaurant(owner, "Urban Tiffin", "Fast South Indian breakfasts and homestyle meals.", "South Indian", "Bengaluru");
        addMenu(urbanTiffin, "Breakfast", new Object[][]{
                {"Masala Dosa", "Crisp dosa with potato masala and chutneys.", "120.00", true, 15},
                {"Idli Vada Combo", "Soft idlis, vada, sambar, and coconut chutney.", "105.00", true, 12}
        });
        addMenu(urbanTiffin, "Meals", new Object[][]{
                {"Mini Meals", "Rice, sambar, rasam, poriyal, curd, and pickle.", "160.00", true, 18}
        });

        Restaurant wokStreet = createRestaurant(owner, "Wok Street", "Indo-Chinese bowls, noodles, and quick wok plates.", "Chinese", "Bengaluru");
        addMenu(wokStreet, "Noodles", new Object[][]{
                {"Veg Hakka Noodles", "Street-style noodles tossed with crunchy vegetables.", "190.00", true, 18},
                {"Chicken Schezwan Noodles", "Spicy noodles with chicken and Schezwan sauce.", "240.00", false, 20}
        });
        addMenu(wokStreet, "Starters", new Object[][]{
                {"Chilli Paneer", "Crispy paneer tossed with peppers and soy chilli glaze.", "230.00", true, 18}
        });

        Restaurant crustLab = createRestaurant(owner, "Crust Lab", "Hand-tossed pizzas, loaded sides, and fresh bakes.", "Pizza", "Bengaluru");
        addMenu(crustLab, "Pizzas", new Object[][]{
                {"Margherita Pizza", "Classic tomato, mozzarella, and basil.", "249.00", true, 20},
                {"Peri Peri Chicken Pizza", "Spiced chicken, onions, peppers, and cheese.", "349.00", false, 24}
        });

        Restaurant greenBowl = createRestaurant(owner, "Green Bowl", "Healthy bowls, salads, smoothies, and high-protein plates.", "Healthy", "Bengaluru");
        addMenu(greenBowl, "Bowls", new Object[][]{
                {"Protein Buddha Bowl", "Quinoa, chickpeas, greens, hummus, and tahini.", "260.00", true, 15},
                {"Grilled Chicken Salad", "Grilled chicken, lettuce, olives, and herb dressing.", "290.00", false, 16}
        });

        couponRepository.findByCodeIgnoreCase("WELCOME50")
                .orElseGet(() -> couponRepository.save(Coupon.builder()
                        .code("WELCOME50")
                        .discountType(DiscountType.FLAT)
                        .discountValue(new BigDecimal("50.00"))
                        .minOrderAmount(new BigDecimal("250.00"))
                        .maxDiscountAmount(new BigDecimal("50.00"))
                        .validFrom(Instant.now().minusSeconds(3600))
                        .validUntil(Instant.now().plusSeconds(60L * 60 * 24 * 30))
                        .active(true)
                        .build()));
    }

    private Restaurant createRestaurant(User owner, String name, String description, String cuisine, String city) {
        return restaurantRepository.save(Restaurant.builder()
                .owner(owner)
                .name(name)
                .description(description)
                .cuisineType(cuisine)
                .phone("9800000000")
                .email(name.toLowerCase().replace(" ", "") + "@foodhub.local")
                .city(city)
                .status(RestaurantStatus.APPROVED)
                .open(true)
                .averageRating(new BigDecimal("4.50"))
                .build());
    }

    private void addMenu(Restaurant restaurant, String categoryName, Object[][] items) {
        FoodCategory category = foodCategoryRepository.save(FoodCategory.builder()
                .restaurant(restaurant)
                .name(categoryName)
                .active(true)
                .build());

        for (Object[] item : items) {
            foodItemRepository.save(FoodItem.builder()
                    .restaurant(restaurant)
                    .category(category)
                    .name((String) item[0])
                    .description((String) item[1])
                    .price(new BigDecimal((String) item[2]))
                    .vegetarian((Boolean) item[3])
                    .available(true)
                    .preparationTimeMinutes((Integer) item[4])
                    .averageRating(new BigDecimal("4.40"))
                    .build());
        }
    }
}
