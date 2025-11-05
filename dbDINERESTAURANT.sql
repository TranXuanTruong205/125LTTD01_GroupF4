-- ============================================
-- DATABASE SCHEMA - ỨNG DỤNG ĐẶT MÓN ĂN
-- ============================================

-- Tạo database
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'dbDINERESTAURANT')
BEGIN
    ALTER DATABASE dbDINERESTAURANT SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE dbDINERESTAURANT;
END

CREATE DATABASE dbDINERESTAURANT;

GO

USE dbDINERESTAURANT;
GO

-- ============================================
-- 1. BẢNG NGƯỜI DÙNG
-- ============================================

CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY(1,1),
    phone_number NVARCHAR(15) UNIQUE NOT NULL,
    email NVARCHAR(100),
    full_name NVARCHAR(100),
    gender NVARCHAR(10) CHECK (gender IN (N'Nam', N'Nữ', N'Khác')),
    address NVARCHAR(MAX),
    date_of_birth DATE,
    profile_picture NVARCHAR(255),
    role NVARCHAR(20) CHECK (role IN ('customer', 'admin')) DEFAULT 'customer',
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    last_login DATETIME
);
GO

-- ============================================
-- BẢNG XÁC THỰC OTP (cho đăng nhập không mật khẩu)
-- ============================================

CREATE TABLE otp_codes (
    otp_id INT PRIMARY KEY IDENTITY(1,1),
    phone_number NVARCHAR(15) NOT NULL,
    otp_code NVARCHAR(6) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    expires_at DATETIME NOT NULL,
    is_verified BIT DEFAULT 0,
    attempt_count INT DEFAULT 0
);
GO

-- ============================================
-- 2. BẢNG DANH MỤC MÓN ĂN
-- ============================================

CREATE TABLE categories (
    category_id INT PRIMARY KEY IDENTITY(1,1),
    category_name NVARCHAR(50) NOT NULL,
    icon NVARCHAR(100),
    display_order INT DEFAULT 0
);
GO

-- ============================================
-- 3. BẢNG MÓN ĂN
-- ============================================

CREATE TABLE menu_items (
    item_id INT PRIMARY KEY IDENTITY(1,1),
    category_id INT NOT NULL,
    item_name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),
    price DECIMAL(10, 2) NOT NULL,
    discount_price DECIMAL(10, 2),
    image NVARCHAR(255),
    rating DECIMAL(2, 1) DEFAULT 0.0,
    total_reviews INT DEFAULT 0,
    is_available BIT DEFAULT 1,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
GO

-- ============================================
-- 4. BẢNG TÙY CHỌN MÓN ĂN (Topping)
-- ============================================

CREATE TABLE item_options (
    option_id INT PRIMARY KEY IDENTITY(1,1),
    item_id INT NOT NULL,
    option_name NVARCHAR(50) NOT NULL,
    extra_price DECIMAL(10, 2) DEFAULT 0,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id) ON DELETE CASCADE
);
GO

-- ============================================
-- 5. BẢNG BÀN
-- ============================================

CREATE TABLE tables (
    table_id INT PRIMARY KEY IDENTITY(1,1),
    table_number NVARCHAR(10) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    status NVARCHAR(20) CHECK (status IN (N'Trống', N'Đang sử dụng', N'Đã đặt')) DEFAULT N'Trống'
);
GO

-- ============================================
-- 6. BẢNG ĐẶT BÀN
-- ============================================

CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    table_id INT NOT NULL,
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    guest_count INT NOT NULL,
    note NVARCHAR(MAX),
    status NVARCHAR(20) CHECK (status IN (N'Chờ xác nhận', N'Đã xác nhận', N'Đã hủy', N'Hoàn thành')) DEFAULT N'Chờ xác nhận',
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
);
GO

-- ============================================
-- 7. BẢNG ĐƠN HÀNG
-- ============================================

CREATE TABLE orders (
    order_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    order_type NVARCHAR(20) CHECK (order_type IN (N'Tại chỗ', N'Giao hàng', N'Mang về')) NOT NULL,
    table_id INT,
    delivery_address NVARCHAR(MAX),
    total_amount DECIMAL(10, 2) NOT NULL,
    delivery_fee DECIMAL(10, 2) DEFAULT 0,
    payment_method NVARCHAR(20) CHECK (payment_method IN (N'Tiền mặt', N'Chuyển khoản')) DEFAULT N'Tiền mặt',
    order_status NVARCHAR(20) CHECK (order_status IN (N'Đã đặt', N'Đã xác nhận', N'Đang chuẩn bị', N'Đang giao', N'Hoàn thành', N'Đã hủy')) DEFAULT N'Đã đặt',
    note NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
);
GO

-- ============================================
-- 8. BẢNG CHI TIẾT ĐƠN HÀNG
-- ============================================

CREATE TABLE order_details (
    detail_id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
);
GO

-- ============================================
-- 9. BẢNG TÙY CHỌN ĐÃ CHỌN TRONG ĐƠN HÀNG
-- ============================================

CREATE TABLE order_detail_options (
    id INT PRIMARY KEY IDENTITY(1,1),
    detail_id INT NOT NULL,
    option_id INT NOT NULL,
    FOREIGN KEY (detail_id) REFERENCES order_details(detail_id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES item_options(option_id)
);
GO

-- ============================================
-- 10. BẢNG ĐÁNH GIÁ
-- ============================================

CREATE TABLE reviews (
    review_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    order_id INT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
GO

-- ============================================
-- 11. BẢNG GIỎ HÀNG
-- ============================================

CREATE TABLE cart (
    cart_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL UNIQUE,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO

-- ============================================
-- 12. BẢNG CHI TIẾT GIỎ HÀNG
-- ============================================

CREATE TABLE cart_items (
    cart_item_id INT PRIMARY KEY IDENTITY(1,1),
    cart_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
);
GO

-- ============================================
-- 13. BẢNG KHUYẾN MÃI
-- ============================================

CREATE TABLE promotions (
    promotion_id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    image NVARCHAR(255),
    discount_percent INT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BIT DEFAULT 1
);
GO

-- ============================================
-- 14. BẢNG THÔNG BÁO
-- ============================================

CREATE TABLE notifications (
    notification_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT,
    title NVARCHAR(200) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    type NVARCHAR(20) CHECK (type IN (N'Đơn hàng', N'Khuyến mãi', N'Đặt bàn', N'Hệ thống')) NOT NULL,
    is_read BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO

-- ============================================
-- TẠO INDEX ĐỂ TỐI ƯU HIỆU SUẤT
-- ============================================

CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_menu_category ON menu_items(category_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_reservations_date ON reservations(reservation_date, reservation_time);
CREATE INDEX idx_reviews_item ON reviews(item_id);
GO

-- ============================================
-- THÊM DỮ LIỆU MẪU (SAMPLE DATA)
-- ============================================

-- 1. Thêm người dùng mẫu
INSERT INTO users (phone_number, email, full_name, gender, address, role) VALUES
(N'+84123456789', N'admin@restaurant.com', N'Quản Trị Viên', N'Nam', N'Số 28 Cao Thắng, Tp Đà Nẵng', 'admin'),
(N'+84987654321', N'xuantruong277@gmail.com', N'Trần Xuân Trường', N'Nam', N'Số 28 Cao Thắng, Tp Đà Nẵng', 'customer'),
(N'+84901234567', N'nguyenvana@gmail.com', N'Nguyễn Văn A', N'Nam', N'123 Lê Duẩn, Đà Nẵng', 'customer'),
(N'+84912345678', N'tranthib@gmail.com', N'Trần Thị B', N'Nữ', N'456 Trần Phú, Đà Nẵng', 'customer');
GO

-- 2. Thêm danh mục món ăn
INSERT INTO categories (category_name, icon, display_order) VALUES
(N'Burger', N'burger_icon.png', 1),
(N'Taco', N'taco_icon.png', 2),
(N'Burrito', N'burrito_icon.png', 3),
(N'Drink', N'drink_icon.png', 4),
(N'Pizza', N'pizza_icon.png', 5),
(N'Donut', N'donut_icon.png', 6),
(N'Salad', N'salad_icon.png', 7),
(N'Noodles', N'noodles_icon.png', 8),
(N'Sandwich', N'sandwich_icon.png', 9),
(N'Pasta', N'pasta_icon.png', 10),
(N'Ice Cream', N'icecream_icon.png', 11);
GO

-- 3. Thêm món ăn mẫu
INSERT INTO menu_items (category_id, item_name, description, price, discount_price, image, rating, total_reviews, is_available) VALUES
-- Burger
(1, N'Chicken Burger', N'A delicious chicken burger served on a toasted bun with fresh lettuce, tomato slices, and mayonnaise. Juicy grilled chicken patty seasone...', 50000, 40000, N'chicken_burger.jpg', 4.9, 1205, 1),
(1, N'Beef Burger', N'Burger bò nướng thơm ngon với phô mai, rau xà lách, cà chua', 60000, 55000, N'beef_burger.jpg', 4.7, 856, 1),
(1, N'Cheese Burger', N'Burger phô mai đặc biệt với 2 lớp phô mai tan chảy', 55000, NULL, N'cheese_burger.jpg', 4.8, 642, 1),

-- Pizza
(5, N'Pepperoni Pizza', N'Pizza xúc xích Ý cay nồng với phô mai mozzarella', 120000, 100000, N'pepperoni_pizza.jpg', 4.6, 523, 1),
(5, N'Hawaiian Pizza', N'Pizza Hawaii với thịt nguội và dứa', 110000, NULL, N'hawaiian_pizza.jpg', 4.5, 412, 1),
(5, N'Seafood Pizza', N'Pizza hải sản tươi ngon với tôm, mực, nghêu', 150000, 135000, N'seafood_pizza.jpg', 4.8, 389, 1),

-- Drink
(4, N'Coca Cola', N'Nước ngọt có gas', 15000, NULL, N'coca.jpg', 4.5, 1520, 1),
(4, N'Pepsi', N'Nước ngọt có gas', 15000, NULL, N'pepsi.jpg', 4.4, 1234, 1),
(4, N'Trà Đào', N'Trà đào cam sả tươi mát', 25000, 20000, N'tra_dao.jpg', 4.7, 892, 1),
(4, N'Sinh Tố Bơ', N'Sinh tố bơ sánh mịn', 30000, NULL, N'sinh_to_bo.jpg', 4.6, 654, 1),

-- Ice Cream
(11, N'Vanilla Ice Cream', N'Kem vani nguyên chất', 25000, 15000, N'vanilla_icecream.jpg', 4.5, 432, 1),
(11, N'Chocolate Ice Cream', N'Kem chocolate đậm đà', 25000, 15000, N'chocolate_icecream.jpg', 4.6, 521, 1),
(11, N'Strawberry Ice Cream', N'Kem dâu tây tươi ngon', 30000, 18000, N'strawberry_icecream.jpg', 4.7, 398, 1),

-- Pasta
(10, N'Spaghetti Carbonara', N'Mì Ý sốt kem trứng và thịt xông khói', 65000, NULL, N'carbonara.jpg', 4.8, 756, 1),
(10, N'Spaghetti Bolognese', N'Mì Ý sốt thịt bò băm cà chua', 60000, 55000, N'bolognese.jpg', 4.7, 689, 1),

-- Salad
(7, N'Caesar Salad', N'Salad Caesar với gà nướng và sốt đặc biệt', 45000, 40000, N'caesar_salad.jpg', 4.6, 345, 1),
(7, N'Greek Salad', N'Salad Hy Lạp với phô mai feta', 50000, NULL, N'greek_salad.jpg', 4.5, 287, 1);
GO

-- 4. Thêm tùy chọn món ăn (Add-ons)
INSERT INTO item_options (item_id, option_name, extra_price) VALUES
-- Chicken Burger options
(1, N'Add Cheese', 12500),
(1, N'Add Bacon', 25000),
(1, N'Add Egg', 10000),
-- Beef Burger options
(2, N'Add Cheese', 12500),
(2, N'Add Bacon', 25000),
-- Pizza options
(4, N'Extra Cheese', 20000),
(4, N'Extra Pepperoni', 30000),
(5, N'Extra Cheese', 20000),
(6, N'Extra Seafood', 40000);
GO

-- 5. Thêm bàn
INSERT INTO tables (table_number, capacity, status) VALUES
(N'Table 01', 4, N'Trống'),
(N'Table 02', 2, N'Trống'),
(N'Table 03', 6, N'Đang sử dụng'),
(N'Table 04', 4, N'Đã đặt'),
(N'Table 05', 4, N'Trống'),
(N'Table 06', 8, N'Trống'),
(N'Table 07', 2, N'Trống'),
(N'Table 08', 4, N'Trống');
GO

-- 6. Thêm đặt bàn mẫu
INSERT INTO reservations (user_id, table_id, reservation_date, reservation_time, guest_count, note, status) VALUES
(2, 4, '2024-11-10', '18:00:00', 4, N'Cần ghế cao cho trẻ em', N'Đã xác nhận'),
(3, 3, '2024-11-08', '19:00:00', 2, NULL, N'Hoàn thành'),
(4, 2, '2024-11-12', '12:00:00', 2, N'Ngồi gần cửa sổ', N'Chờ xác nhận');
GO

-- 7. Thêm đơn hàng mẫu
INSERT INTO orders (user_id, order_type, table_id, delivery_address, total_amount, delivery_fee, payment_method, order_status, note) VALUES
(2, N'Tại chỗ', 3, NULL, 155000, 0, N'Tiền mặt', N'Hoàn thành', NULL),
(3, N'Giao hàng', NULL, N'123 Lê Duẩn, Đà Nẵng', 280000, 20000, N'Chuyển khoản', N'Đang giao', N'Gọi trước khi giao'),
(4, N'Mang về', NULL, NULL, 85000, 0, N'Tiền mặt', N'Đã xác nhận', NULL),
(2, N'Giao hàng', NULL, N'Số 28 Cao Thắng, Tp Đà Nẵng', 120000, 15000, N'Tiền mặt', N'Đã đặt', NULL);
GO

-- 8. Thêm chi tiết đơn hàng
-- Đơn hàng 1 (Tại chỗ)
INSERT INTO order_details (order_id, item_id, quantity, unit_price, subtotal) VALUES
(1, 1, 2, 40000, 80000),
(1, 7, 3, 15000, 45000),
(1, 9, 1, 20000, 20000);

-- Đơn hàng 2 (Giao hàng)
INSERT INTO order_details (order_id, item_id, quantity, unit_price, subtotal) VALUES
(2, 4, 2, 100000, 200000),
(2, 9, 2, 20000, 40000);

-- Đơn hàng 3 (Mang về)
INSERT INTO order_details (order_id, item_id, quantity, unit_price, subtotal) VALUES
(3, 2, 1, 55000, 55000),
(3, 7, 2, 15000, 30000);

-- Đơn hàng 4 (Giao hàng)
INSERT INTO order_details (order_id, item_id, quantity, unit_price, subtotal) VALUES
(4, 1, 2, 40000, 80000),
(4, 9, 2, 20000, 40000);
GO

-- 9. Thêm đánh giá
INSERT INTO reviews (user_id, item_id, order_id, rating, comment) VALUES
(2, 1, 1, 5, N'Delicious chicken burger! Loved the crispy chicken and the bun was perfectly toasted. Definitely a new favorite!'),
(3, 1, NULL, 5, N'Absolutely delicious! The chicken burger was juicy and flavorful, with just the right amount of seasoning. Highly recommend!'),
(4, 1, NULL, 5, N'One of the best chicken burgers I''ve ever had! The chicken was tender and the bun was soft. Loved every bite!'),
(2, 4, 2, 5, N'Pizza tuyệt vời, phô mai thơm ngon'),
(3, 2, 3, 4, N'Burger bò ngon nhưng hơi nhỏ'),
(4, 9, NULL, 5, N'Trà đào rất tươi mát, phù hợp mùa hè');
GO

-- 10. Thêm giỏ hàng mẫu
INSERT INTO cart (user_id) VALUES (2), (3);
GO

INSERT INTO cart_items (cart_id, item_id, quantity) VALUES
(1, 1, 1),
(1, 7, 2),
(2, 4, 1),
(2, 11, 2);
GO

-- 11. Thêm khuyến mãi
INSERT INTO promotions (title, description, image, discount_percent, start_date, end_date, is_active) VALUES
(N'ICE CREAM DAY', N'GET YOUR SWEET ICE CREAM - 40% OFF', N'icecream_promo.jpg', 40, '2024-11-01', '2024-11-15', 1),
(N'PIZZA WEEKEND', N'Giảm 20% tất cả các loại Pizza', N'pizza_promo.jpg', 20, '2024-11-08', '2024-11-10', 1),
(N'COMBO BURGER', N'Mua 2 Burger tặng 1 Drink', N'burger_combo.jpg', 0, '2024-11-01', '2024-11-30', 1);
GO

-- 12. Thêm thông báo
INSERT INTO notifications (user_id, title, message, type, is_read) VALUES
(2, N'Đơn hàng đã hoàn thành', N'Đơn hàng #1 của bạn đã được hoàn thành. Cảm ơn bạn đã sử dụng dịch vụ!', N'Đơn hàng', 1),
(2, N'Khuyến mãi mới', N'ICE CREAM DAY - Giảm 40% tất cả kem! Nhanh tay đặt hàng!', N'Khuyến mãi', 0),
(3, N'Đơn hàng đang giao', N'Đơn hàng #2 của bạn đang trên đường giao đến. Dự kiến 15 phút nữa.', N'Đơn hàng', 0),
(4, N'Đặt bàn thành công', N'Bạn đã đặt bàn Table 02 vào 12:00 ngày 12/11/2024', N'Đặt bàn', 1);
GO

-- ============================================
-- KẾT THÚC SCHEMA VÀ DỮ LIỆU MẪU
-- ============================================