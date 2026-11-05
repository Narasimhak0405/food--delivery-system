# 🍕 FoodieExpress — Online Food Delivery Web Application

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Security](https://img.shields.io/badge/Spring%20Security-JWT-red.svg)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

FoodieExpress is a professional, full-stack online food delivery platform inspired by Swiggy and Zomato. It integrates **HTML5/CSS3/JavaScript** on the frontend, a **Spring Boot 3** RESTful micro-architecture on the backend, and **MySQL 8.0** for relational persistence, secured with **JWT & BCrypt** authentication and **Razorpay Test Mode** payments.

---

## 🌟 Key Features

### Customer Experience
- 🔍 **Live Search & Category Taxonomy**: Search across restaurants, cuisines, and food items.
- 🛒 **Dynamic Shopping Cart**: Real-time quantity adjustments, GST calculation, and delivery fees.
- 📍 **Address Selection**: Choose between saved addresses (*Home*, *Work*) or add delivery notes.
- 💳 **Razorpay Integration**: Simulated test-mode checkout supporting UPI, Cards, and Netbanking.
- 📦 **Live Order Tracking**: Visual 4-stage tracking (*Placed* &rarr; *Confirmed* &rarr; *Preparing* &rarr; *Out for Delivery* &rarr; *Delivered*).
- ❌ **Order Cancellation**: Validated cancellation window with automated refund initiation.

### Partner & Admin Portals
- 👨‍🍳 **Restaurant Kitchen Console (`/restaurant-portal.html`)**: Real-time kitchen queue, 1-click order state advancement, and iOS-style instant dish availability toggle (*In Stock* / *Sold Out*).
- ⚡ **Master Admin Command Center (`/admin.html`)**: Platform revenue analytics, restaurant onboarding, category taxonomy, and role management.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | HTML5, CSS3 (Modern Flexbox/Grid), Vanilla JavaScript (ES6+), Fetch API, LocalStorage |
| **Backend** | Java 17, Spring Boot 3.2.5, Spring MVC, Spring Data JPA, Hibernate, Spring Security 6 |
| **Database** | MySQL 8.0 (InnoDB Engine, UTF-8mb4) |
| **Security** | JWT (JSON Web Tokens - JJWT 0.12.5), BCrypt Password Hashing, RBAC |
| **Payment Gateway** | Razorpay Test Mode Gateway (HMAC-SHA256 Signature Verification) |
| **DevOps** | Docker, Docker-Compose, Multi-Stage Builds |

---

## 🚀 Quick Start Guide

### 1. Prerequisites
- **JDK 17** or higher
- **MySQL Server 8.0+**
- **Maven** (bundled or standalone) or **Docker**

### 2. Database Setup
1. Open MySQL Workbench or MySQL CLI:
   ```bash
   mysql -u root -p < database_setup.sql
   ```
2. Confirm database connection in `src/main/resources/application.properties`.

### 3. Run Locally (IDE or CLI)
```bash
# Using Maven
mvn clean spring-boot:run

# Or run FoodieExpressApplication.java from IntelliJ IDEA / Eclipse
```
Access the application at: **[http://localhost:8080/](http://localhost:8080/)**

### 4. Run via Docker Compose (Single Command)
```bash
docker-compose up --build
```

---

## 🔑 Demo Credentials

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `admin@foodieexpress.com` | `password123` | Full Master Control (`/admin.html`) |
| **Customer** | `rahul@gmail.com` | `password123` | Cart, Checkout, Order Tracking |
| **Restaurant Partner** | `marco@bellarestaurant.com` | `password123` | Kitchen Console (`/restaurant-portal.html`) |

---

## 📚 REST API Collection
Import the ready-made collection into Postman:  
[`FoodieExpress_Postman_Collection.json`](FoodieExpress_Postman_Collection.json)

