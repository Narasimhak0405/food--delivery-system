/* ==========================================================================
   FoodieExpress - Master Frontend JavaScript (Cart, API, Storage, State)
   ========================================================================== */

// 1. Initial Mock Data (Synchronized with Backend MySQL Schema)
const DEFAULT_RESTAURANTS = [
  {
    id: 1,
    name: "Spice Paradise",
    cuisineType: "Biryani, Mughlai, North Indian",
    rating: 4.8,
    deliveryTimeMins: 30,
    minOrderAmount: 200,
    address: "45 100 Feet Road, Indiranagar",
    city: "Bangalore",
    imageUrl: "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=600"
  },
  {
    id: 2,
    name: "Bella Italia Trattoria",
    cuisineType: "Italian, Pizza, Pasta",
    rating: 4.6,
    deliveryTimeMins: 25,
    minOrderAmount: 250,
    address: "12 Brigade Road, Ashok Nagar",
    city: "Bangalore",
    imageUrl: "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600"
  },
  {
    id: 3,
    name: "Burger Craft Co.",
    cuisineType: "Burgers, American, Fast Food",
    rating: 4.5,
    deliveryTimeMins: 20,
    minOrderAmount: 150,
    address: "88 Koramangala 4th Block",
    city: "Bangalore",
    imageUrl: "https://images.unsplash.com/photo-1550547660-d9450f859349?w=600"
  },
  {
    id: 4,
    name: "Dragon Bowl Express",
    cuisineType: "Chinese, Asian, Dim Sum",
    rating: 4.7,
    deliveryTimeMins: 35,
    minOrderAmount: 180,
    address: "21 Church Street, Central",
    city: "Bangalore",
    imageUrl: "https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=600"
  },
  {
    id: 5,
    name: "The Sugar Bloom Cakery",
    cuisineType: "Desserts, Bakery, Shakes",
    rating: 4.9,
    deliveryTimeMins: 20,
    minOrderAmount: 120,
    address: "104 Lavelle Road",
    city: "Bangalore",
    imageUrl: "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600"
  }
];

const DEFAULT_FOOD_ITEMS = [
  { id: 1, restaurantId: 1, name: "Royal Chicken Dum Biryani", category: "Biryani", price: 349, isVeg: false, description: "Slow-cooked fragrant basmati rice with succulent chicken marinated in exotic spices.", imageUrl: "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600" },
  { id: 2, restaurantId: 1, name: "Paneer Tikka Biryani", category: "Biryani", price: 299, isVeg: true, description: "Charcoal-grilled cottage cheese cubes layered with saffron spiced basmati rice.", imageUrl: "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=600" },
  { id: 3, restaurantId: 1, name: "Butter Chicken Masala", category: "North Indian", price: 320, isVeg: false, description: "Tender tandoori chicken cooked in a rich, buttery tomato cashew gravy.", imageUrl: "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=600" },
  { id: 4, restaurantId: 1, name: "Garlic Butter Naan (2 Pcs)", category: "Breads", price: 80, isVeg: true, description: "Clay oven baked flatbread brushed with crushed garlic and melted butter.", imageUrl: "https://images.unsplash.com/photo-1626074353765-517a681e40be?w=600" },

  { id: 5, restaurantId: 2, name: "Margherita Con Bufala Pizza", category: "Pizza", price: 399, isVeg: true, description: "San Marzano sauce, fresh buffalo mozzarella, basil, extra virgin olive oil.", imageUrl: "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=600" },
  { id: 6, restaurantId: 2, name: "Spicy Pepperoni & Jalapeno Pizza", category: "Pizza", price: 479, isVeg: false, description: "Imported pepperoni, spicy jalapeno slices, and chili honey drizzle.", imageUrl: "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=600" },
  { id: 7, restaurantId: 2, name: "Creamy Truffle Mushroom Penne", category: "Pasta", price: 380, isVeg: true, description: "Al dente penne tossed in wild porcini mushroom puree and parmesan cream.", imageUrl: "https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=600" },

  { id: 8, restaurantId: 3, name: "The Classic Smash Cheese Burger", category: "Burgers", price: 259, isVeg: false, description: "Double smashed patty, melted sharp cheddar, caramelized onions in brioche.", imageUrl: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600" },
  { id: 9, restaurantId: 3, name: "Crispy Paneer Tower Burger", category: "Burgers", price: 229, isVeg: true, description: "Panko crusted spicy paneer patty, crunchy slaw, and chipotle mayo.", imageUrl: "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=600" },
  { id: 10, restaurantId: 3, name: "Loaded Peri-Peri Cheese Fries", category: "Sides", price: 149, isVeg: true, description: "Golden French fries dusted with peri-peri seasoning and cheese sauce.", imageUrl: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=600" },

  { id: 11, restaurantId: 4, name: "Chili Garlic Chicken Hakka Noodles", category: "Noodles", price: 260, isVeg: false, description: "Wok-tossed noodles with shredded chicken, crunchy scallions, and roasted garlic.", imageUrl: "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=600" },
  { id: 12, restaurantId: 4, name: "Steamed Crystal Veg Dim Sum (6 Pcs)", category: "Dim Sum", price: 210, isVeg: true, description: "Translucent dumplings filled with water chestnuts and mushrooms with chili dip.", imageUrl: "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=600" },

  { id: 13, restaurantId: 5, name: "New York Baked Cheesecake Slice", category: "Desserts", price: 220, isVeg: true, description: "Classic dense and creamy cheesecake on a buttery graham cracker crust.", imageUrl: "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=600" },
  { id: 14, restaurantId: 5, name: "Belgian Chocolate Truffle Shake", category: "Beverages", price: 180, isVeg: true, description: "Rich thick shake made with Belgian dark chocolate and creamy gelato.", imageUrl: "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=600" }
];

// ---------------------------------------------------------------------------
// 2. Cart Management State
// ---------------------------------------------------------------------------
function getCart() {
  const cart = localStorage.getItem('foodie_cart');
  return cart ? JSON.parse(cart) : [];
}

function saveCart(cart) {
  localStorage.setItem('foodie_cart', JSON.stringify(cart));
  updateCartBadge();
}

function addToCart(item) {
  const cart = getCart();
  const existing = cart.find(ci => ci.id === item.id);
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push({ ...item, quantity: 1 });
  }
  saveCart(cart);
  showToast(`Added "${item.name}" to cart! 🛒`);
}

function updateCartQuantity(itemId, delta) {
  let cart = getCart();
  const item = cart.find(ci => ci.id === itemId);
  if (item) {
    item.quantity += delta;
    if (item.quantity <= 0) {
      cart = cart.filter(ci => ci.id !== itemId);
    }
  }
  saveCart(cart);
}

function clearCart() {
  localStorage.removeItem('foodie_cart');
  updateCartBadge();
}

function getCartCalculations() {
  const cart = getCart();
  const itemTotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const deliveryFee = itemTotal > 0 ? 40 : 0;
  const platformFee = itemTotal > 0 ? 5 : 0;
  const taxAmount = Math.round(itemTotal * 0.05); // 5% GST
  const grandTotal = itemTotal + deliveryFee + platformFee + taxAmount;
  return { itemTotal, deliveryFee, platformFee, taxAmount, grandTotal };
}

function updateCartBadge() {
  const badge = document.getElementById('cartBadge');
  if (badge) {
    const cart = getCart();
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    badge.textContent = count;
  }
}

// ---------------------------------------------------------------------------
// 3. User Session Management
// ---------------------------------------------------------------------------
function getCurrentUser() {
  const user = localStorage.getItem('foodie_user');
  if (user) return JSON.parse(user);
  
  // Default demo user
  const defaultUser = {
    id: 2,
    name: "Rahul Sharma",
    email: "rahul@gmail.com",
    phone: "9811122233",
    address: "Flat 402, Sunshine Heights, 12th Main, Indiranagar, Bangalore"
  };
  localStorage.setItem('foodie_user', JSON.stringify(defaultUser));
  return defaultUser;
}

function getAuthToken() {
  return localStorage.getItem('foodie_token');
}

function setAuthSession(user, token) {
  if (user) localStorage.setItem('foodie_user', JSON.stringify(user));
  if (token) localStorage.setItem('foodie_token', token);
}

function logoutUser() {
  localStorage.removeItem('foodie_user');
  localStorage.removeItem('foodie_token');
  showToast("Logged out successfully.");
  setTimeout(() => {
    window.location.href = 'login.html';
  }, 600);
}

async function authFetch(url, options = {}) {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(url, { ...options, headers });
  if (response.status === 401) {
    showToast("Session expired. Please log in again.");
    logoutUser();
  }
  return response;
}

// ---------------------------------------------------------------------------
// 4. Order Management
// ---------------------------------------------------------------------------
function getOrders() {
  const orders = localStorage.getItem('foodie_orders');
  return orders ? JSON.parse(orders) : [
    {
      orderNumber: "ORD-2026-9041",
      restaurantName: "Spice Paradise",
      items: [
        { name: "Royal Chicken Dum Biryani", quantity: 2, price: 349 },
        { name: "Garlic Butter Naan (2 Pcs)", quantity: 2, price: 80 }
      ],
      totalAmount: 902.90,
      status: "DELIVERED",
      date: "Nov 5, 2026, 07:30 PM",
      address: "Flat 402, Sunshine Heights, Indiranagar, Bangalore"
    }
  ];
}

function placeOrder(orderDetails) {
  const orders = getOrders();
  orders.unshift(orderDetails);
  localStorage.setItem('foodie_orders', JSON.stringify(orders));
  clearCart();
}

// ---------------------------------------------------------------------------
// 5. Toast Notification Utility
// ---------------------------------------------------------------------------
function showToast(message) {
  let toast = document.getElementById('toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'toast';
    document.body.appendChild(toast);
  }
  toast.textContent = message;
  toast.style.display = 'block';
  setTimeout(() => {
    toast.style.display = 'none';
  }, 3000);
}

// ---------------------------------------------------------------------------
// 6. Navigation Bar & Footer Generator (Ensures Consistency Across All Pages)
// ---------------------------------------------------------------------------
function renderHeader(activePage = '') {
  const headerContainer = document.getElementById('navbarContainer');
  if (!headerContainer) return;

  const user = getCurrentUser();
  const cart = getCart();
  const count = cart.reduce((sum, item) => sum + item.quantity, 0);

  headerContainer.innerHTML = `
    <nav class="navbar">
      <div class="nav-container">
        <a href="index.html" class="brand">
          🍕 Foodie<span>Express</span>
        </a>

        <div class="location-badge" title="Delivery Address">
          📍 <span>Indiranagar, Bangalore</span> ▾
        </div>

        <div class="nav-search">
          <span class="search-icon">🔍</span>
          <input type="text" id="globalSearch" placeholder="Search for restaurant, cuisine, or a dish..." onkeyup="handleGlobalSearch(event)">
        </div>

        <ul class="nav-links">
          <li><a href="index.html" class="nav-link ${activePage === 'home' ? 'active' : ''}">Home</a></li>
          <li><a href="restaurants.html" class="nav-link ${activePage === 'restaurants' ? 'active' : ''}">Restaurants</a></li>
          <li><a href="orders.html" class="nav-link ${activePage === 'orders' ? 'active' : ''}">Orders</a></li>
          <li><a href="restaurant-portal.html" class="nav-link" style="color: #e67e22;" title="Kitchen Portal">👨‍🍳 Kitchen</a></li>
          <li><a href="admin.html" class="nav-link" style="color: #e74c3c;" title="Admin Dashboard">⚡ Admin</a></li>
          <li>
            <a href="cart.html" class="cart-btn nav-link">
              🛒 Cart <span id="cartBadge" class="cart-badge">${count}</span>
            </a>
          </li>
          <li>
            <a href="profile.html" class="nav-link ${activePage === 'profile' ? 'active' : ''}">
              👤 ${user ? user.name.split(' ')[0] : 'Sign In'}
            </a>
          </li>
        </ul>
      </div>
    </nav>
  `;
}

function renderFooter() {
  const footerContainer = document.getElementById('footerContainer');
  if (!footerContainer) return;

  footerContainer.innerHTML = `
    <footer>
      <div class="footer-container">
        <div class="footer-col">
          <div class="brand" style="margin-bottom: 1rem;">🍕 Foodie<span style="color: #fc8019;">Express</span></div>
          <p style="line-height: 1.6; font-size: 0.92rem;">
            Delivering hot, delicious food right to your doorstep in minutes. Built with Java Spring Boot, MySQL, and modern Web Technologies.
          </p>
        </div>
        <div class="footer-col">
          <h3>Company</h3>
          <ul>
            <li><a href="index.html">About Us</a></li>
            <li><a href="restaurants.html">Our Restaurants</a></li>
            <li><a href="#">FoodieExpress Corporate</a></li>
            <li><a href="#">Careers</a></li>
          </ul>
        </div>
        <div class="footer-col">
          <h3>Help & Legal</h3>
          <ul>
            <li><a href="#">Help & Support</a></li>
            <li><a href="#">Partner with us</a></li>
            <li><a href="#">Terms & Conditions</a></li>
            <li><a href="#">Privacy Policy</a></li>
          </ul>
        </div>
        <div class="footer-col">
          <h3>We Deliver To</h3>
          <ul>
            <li><a href="#">Bangalore</a></li>
            <li><a href="#">Mumbai</a></li>
            <li><a href="#">Hyderabad</a></li>
            <li><a href="#">Delhi NCR</a></li>
          </ul>
        </div>
      </div>
      <div class="footer-bottom">
        FoodieExpress &copy; 2026. All rights reserved. Crafted with ❤️ for Food Lovers.
      </div>
    </footer>
  `;
}

function handleGlobalSearch(e) {
  if (e.key === 'Enter') {
    const query = e.target.value.trim();
    if (query) {
      window.location.href = `restaurants.html?search=${encodeURIComponent(query)}`;
    }
  }
}

// Automatically update cart badge on page load
document.addEventListener('DOMContentLoaded', () => {
  updateCartBadge();
});

