-- ============================================================================
-- FoodieExpress - Production Database Schema & Sample Data
-- Database: MySQL 8.0+
-- ============================================================================

CREATE DATABASE IF NOT EXISTS foodie_express_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE foodie_express_db;

-- ----------------------------------------------------------------------------
-- Drop existing tables in reverse dependency order (to prevent FK conflicts)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS food_items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS users;

-- ============================================================================
-- 1. USERS TABLE
-- Stores customers, restaurant owners, delivery partners, and administrators.
-- ============================================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role ENUM('CUSTOMER', 'RESTAURANT_OWNER', 'DELIVERY_PARTNER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================================
-- 2. ADDRESSES TABLE
-- Stores multiple delivery addresses for users (home, work, other).
-- ============================================================================
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    landmark VARCHAR(100),
    city VARCHAR(80) NOT NULL,
    state VARCHAR(80) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    address_type ENUM('HOME', 'WORK', 'OTHER') DEFAULT 'HOME',
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- 3. RESTAURANTS TABLE
-- Stores partner restaurants, cuisine types, ratings, and operating details.
-- ============================================================================
CREATE TABLE restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    cuisine_type VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(80) NOT NULL,
    rating DECIMAL(2,1) DEFAULT 4.0,
    delivery_time_mins INT DEFAULT 30,
    min_order_amount DECIMAL(10,2) DEFAULT 100.00,
    is_active BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================================
-- 4. CATEGORIES TABLE
-- Stores dish categories (e.g. Biryani, Pizza, Burgers, Desserts).
-- ============================================================================
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================================
-- 5. FOOD_ITEMS TABLE
-- Stores individual dishes offered by restaurants, linked to categories.
-- ============================================================================
CREATE TABLE food_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    is_veg BOOLEAN DEFAULT TRUE,
    is_available BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_food_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    CONSTRAINT fk_food_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================================
-- 6. CART TABLE
-- Stores each user's current active shopping cart (1-to-1 relationship with user).
-- ============================================================================
CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- 7. CART_ITEMS TABLE
-- Line items within a user's active shopping cart.
-- ============================================================================
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    food_item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE CASCADE,
    CONSTRAINT uq_cart_food UNIQUE (cart_id, food_item_id)
) ENGINE=InnoDB;

-- ============================================================================
-- 8. ORDERS TABLE
-- Stores placed orders, status tracking, customer details, and price totals.
-- ============================================================================
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    delivery_address_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    delivery_fee DECIMAL(10,2) DEFAULT 40.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('PLACED', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PLACED',
    special_instructions VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_address FOREIGN KEY (delivery_address_id) REFERENCES addresses(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================================
-- 9. ORDER_ITEMS TABLE
-- Stores snapshots of items in an order (retaining historical price).
-- ============================================================================
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    food_item_id BIGINT,
    item_name VARCHAR(150) NOT NULL,
    item_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_orderitem_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================================
-- 10. PAYMENTS TABLE
-- Stores transaction records for completed, pending, or failed payments.
-- ============================================================================
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'UPI', 'NET_BANKING', 'CASH_ON_DELIVERY') NOT NULL,
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(10,2) NOT NULL,
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- SAMPLE DATA INSERTS
-- ============================================================================

-- 1. Insert Users (Default Password for all sample users: password123)
INSERT INTO users (id, name, email, password_hash, phone, role) VALUES
(1, 'Admin User', 'admin@foodieexpress.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '9876543210', 'ADMIN'),
(2, 'Rahul Sharma', 'rahul@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '9811122233', 'CUSTOMER'),
(3, 'Priya Patel', 'priya@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '9822233344', 'CUSTOMER'),
(4, 'Chef Marco', 'marco@bellarestaurant.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '9833344455', 'RESTAURANT_OWNER'),
(5, 'Sunil Delivery', 'sunil@foodieexpress.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '9844455566', 'DELIVERY_PARTNER');

-- 2. Insert Addresses
INSERT INTO addresses (id, user_id, address_line1, address_line2, landmark, city, state, postal_code, address_type, is_default) VALUES
(1, 2, 'Flat 402, Sunshine Heights', '12th Main Road, HAL 2nd Stage', 'Near Indiranagar Club', 'Bangalore', 'Karnataka', '560038', 'HOME', TRUE),
(2, 2, 'WeWork Prestige Tech Park', 'Outer Ring Road, Kadubeesanahalli', 'Tower B, 3rd Floor', 'Bangalore', 'Karnataka', '560103', 'WORK', FALSE),
(3, 3, 'Villa 15, Palm Meadows', 'Varthur Road, Whitefield', 'Behind Forum Mall', 'Bangalore', 'Karnataka', '560066', 'HOME', TRUE);

-- 3. Insert Categories
INSERT INTO categories (id, name, description, image_url) VALUES
(1, 'Biryani & North Indian', 'Aromatic biryanis, curries, and freshly baked tandoori naans', 'https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=600'),
(2, 'Pizza & Italian', 'Wood-fired pizzas, gourmet pastas, and crispy garlic breads', 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600'),
(3, 'Burgers & Fast Food', 'Juicy grilled burgers, crispy fries, wraps, and finger food', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600'),
(4, 'Chinese & Asian', 'Steaming hot momos, noodles, fried rice, and savory bowls', 'https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=600'),
(5, 'Desserts & Shakes', 'Cakes, waffles, rich ice creams, and refreshing thickshakes', 'https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=600');

-- 4. Insert Restaurants
INSERT INTO restaurants (id, owner_id, name, description, cuisine_type, phone, address, city, rating, delivery_time_mins, min_order_amount, is_active, image_url) VALUES
(1, 4, 'Spice Paradise', 'Authentic royal Hyderabadi Dum Biryanis and rich Mughlai delights crafted by master chefs.', 'Biryani, Mughlai, North Indian', '080-25251122', '45 100 Feet Road, Indiranagar', 'Bangalore', 4.8, 30, 200.00, TRUE, 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=600'),
(2, 4, 'Bella Italia Trattoria', 'Wood-fired artisanal sourdough pizzas, fresh hand-rolled pastas, and classic Italian tiramisu.', 'Italian, Pizza, Pasta', '080-25253344', '12 Brigade Road, Ashok Nagar', 'Bangalore', 4.6, 25, 250.00, TRUE, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600'),
(3, 4, 'Burger Craft Co.', 'Gourmet smashed beef & smash veg patties, brioche buns, and house-made truffle sauce.', 'Burgers, American, Fast Food', '080-25255566', '88 Koramangala 4th Block', 'Bangalore', 4.5, 20, 150.00, TRUE, 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=600'),
(4, 4, 'Dragon Bowl Express', 'Authentic pan-Asian street food, spicy schezwan wok noodles, dim sums, and ramen bowls.', 'Chinese, Asian, Thai', '080-25257788', '21 Church Street, Central', 'Bangalore', 4.7, 35, 180.00, TRUE, 'https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=600'),
(5, 4, 'The Sugar Bloom Cakery', 'Artisan patisserie serving heavenly cheesecakes, Belgian waffles, and gourmet milkshakes.', 'Desserts, Bakery, Shakes', '080-25259900', '104 Lavelle Road', 'Bangalore', 4.9, 20, 120.00, TRUE, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600');

-- 5. Insert Food Items
INSERT INTO food_items (id, restaurant_id, category_id, name, description, price, is_veg, is_available, image_url) VALUES
-- Spice Paradise (Restaurant 1)
(1, 1, 1, 'Royal Chicken Dum Biryani', 'Slow-cooked fragrant basmati rice with succulent chicken marinated in exotic spices, served with Mirchi Ka Salan and Raita.', 349.00, FALSE, TRUE, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600'),
(2, 1, 1, 'Paneer Tikka Biryani', 'Charcoal-grilled cottage cheese cubes layered with saffron spiced basmati rice and caramelized onions.', 299.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=600'),
(3, 1, 1, 'Butter Chicken Masala', 'Tender tandoori chicken cooked in a rich, buttery, velvety tomato cashew gravy.', 320.00, FALSE, TRUE, 'https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=600'),
(4, 1, 1, 'Garlic Butter Naan (2 Pcs)', 'Clay oven baked flatbread brushed with crushed garlic and fresh melted butter.', 80.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1626074353765-517a681e40be?w=600'),

-- Bella Italia (Restaurant 2)
(5, 2, 2, 'Margherita Con Bufala Pizza', 'San Marzano tomato sauce, fresh buffalo mozzarella, fresh basil, and extra virgin olive oil on 48h fermented dough.', 399.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=600'),
(6, 2, 2, 'Spicy Pepperoni & Jalapeno Pizza', 'Imported cured pepperoni, mozzarella, spicy jalapeno slices, and chili honey drizzle.', 479.00, FALSE, TRUE, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=600'),
(7, 2, 2, 'Creamy Truffle Mushroom Penne', 'Al dente penne pasta tossed in wild porcini mushroom puree and parmesan cream sauce.', 380.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=600'),

-- Burger Craft Co. (Restaurant 3)
(8, 3, 3, 'The Classic Smash Cheese Burger', 'Double smashed patty, melted sharp cheddar, caramelized onions, pickles, and secret house sauce in toasted brioche.', 259.00, FALSE, TRUE, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600'),
(9, 3, 3, 'Crispy Paneer Tower Burger', 'Panko crusted spicy paneer patty, crunchy coleslaw, jalapenos, and chipotle mayo.', 229.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=600'),
(10, 3, 3, 'Loaded Peri-Peri Cheese Fries', 'Golden French fries dusted with spicy African peri-peri seasoning and topped with warm cheese sauce.', 149.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=600'),

-- Dragon Bowl Express (Restaurant 4)
(11, 4, 4, 'Chili Garlic Chicken Hakka Noodles', 'Wok-tossed noodles with shredded chicken, crunchy scallions, cabbage, and roasted garlic chili paste.', 260.00, FALSE, TRUE, 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=600'),
(12, 4, 4, 'Steamed Crystal Veg Dim Sum (6 Pcs)', 'Translucent dumplings filled with water chestnuts, mushrooms, and spring onions, served with spicy chili dip.', 210.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=600'),

-- The Sugar Bloom Cakery (Restaurant 5)
(13, 5, 5, 'New York Baked Cheesecake Slice', 'Classic dense and creamy cheesecake on a buttery graham cracker crust with berry compote.', 220.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=600'),
(14, 5, 5, 'Belgian Chocolate Truffle Shake', 'Rich, thick blend of melted Belgian dark chocolate, whole milk, and chocolate gelato.', 180.00, TRUE, TRUE, 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=600');

-- 6. Insert Cart for Customer Rahul (User ID: 2)
INSERT INTO cart (id, user_id) VALUES
(1, 2);

-- 7. Insert Cart Items for Rahul
INSERT INTO cart_items (id, cart_id, food_item_id, quantity) VALUES
(1, 1, 1, 2), -- 2x Royal Chicken Dum Biryani
(2, 1, 4, 2); -- 2x Garlic Butter Naan

-- 8. Insert Sample Orders
INSERT INTO orders (id, order_number, user_id, restaurant_id, delivery_address_id, total_amount, delivery_fee, tax_amount, status, special_instructions) VALUES
(1, 'ORD-2026-001', 2, 1, 1, 898.00, 40.00, 44.90, 'DELIVERED', 'Please provide extra raita and onion rings.'),
(2, 'ORD-2026-002', 3, 2, 3, 439.00, 40.00, 21.95, 'OUT_FOR_DELIVERY', 'Leave parcel at security gate.');

-- 9. Insert Order Items
INSERT INTO order_items (id, order_id, food_item_id, item_name, item_price, quantity, subtotal) VALUES
(1, 1, 1, 'Royal Chicken Dum Biryani', 349.00, 2, 698.00),
(2, 1, 3, 'Butter Chicken Masala', 320.00, 1, 320.00),
(3, 2, 5, 'Margherita Con Bufala Pizza', 399.00, 1, 399.00);

-- 10. Insert Payments
INSERT INTO payments (id, order_id, transaction_id, payment_method, payment_status, amount, payment_time) VALUES
(1, 1, 'TXN_UPI_99881122', 'UPI', 'COMPLETED', 982.90, '2026-11-05 19:30:00'),
(2, 2, 'TXN_CARD_44556677', 'CREDIT_CARD', 'COMPLETED', 500.95, '2026-11-05 20:15:00');

-- ============================================================================
-- VERIFICATION SELECT QUERIES
-- ============================================================================

-- Query 1: Get all restaurants with their active menu items count and category
SELECT 
    r.id AS restaurant_id,
    r.name AS restaurant_name,
    r.cuisine_type,
    r.rating,
    COUNT(f.id) AS total_menu_items
FROM restaurants r
LEFT JOIN food_items f ON r.id = f.restaurant_id
WHERE r.is_active = TRUE
GROUP BY r.id, r.name, r.cuisine_type, r.rating
ORDER BY r.rating DESC;

-- Query 2: Look up full menu for a specific restaurant (e.g. Spice Paradise)
SELECT 
    f.id AS item_id,
    f.name AS dish_name,
    c.name AS category,
    f.price,
    CASE WHEN f.is_veg = 1 THEN 'VEG 🟢' ELSE 'NON-VEG 🔴' END AS food_type,
    f.is_available
FROM food_items f
JOIN categories c ON f.category_id = c.id
WHERE f.restaurant_id = 1
ORDER BY f.price ASC;

-- Query 3: Inspect user shopping cart details with live calculated subtotal
SELECT 
    u.name AS customer_name,
    f.name AS item_name,
    ci.quantity,
    f.price AS unit_price,
    (ci.quantity * f.price) AS total_price
FROM cart c
JOIN users u ON c.user_id = u.id
JOIN cart_items ci ON c.id = ci.cart_id
JOIN food_items f ON ci.food_item_id = f.id
WHERE u.id = 2;

-- Query 4: Detailed Customer Order Summary with Delivery Address and Payment Status
SELECT 
    o.order_number,
    u.name AS customer_name,
    r.name AS restaurant_name,
    CONCAT(a.address_line1, ', ', a.city, ' - ', a.postal_code) AS delivery_address,
    o.status AS order_status,
    p.payment_method,
    p.payment_status,
    o.total_amount
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN restaurants r ON o.restaurant_id = r.id
JOIN addresses a ON o.delivery_address_id = a.id
LEFT JOIN payments p ON o.id = p.order_id
ORDER BY o.created_at DESC;
