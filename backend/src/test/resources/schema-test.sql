-- H2 测试数据库初始化脚本
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS order_record;
DROP TABLE IF EXISTS dish;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    role TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(255),
    description VARCHAR(500),
    difficulty TINYINT NOT NULL DEFAULT 3,
    cook_time INT NOT NULL DEFAULT 30,
    status TINYINT NOT NULL DEFAULT 1,
    order_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    order_date DATE NOT NULL,
    remark VARCHAR(200),
    status TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wishlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dish_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    operation VARCHAR(50) NOT NULL,
    method VARCHAR(200),
    params TEXT,
    ip VARCHAR(50),
    cost_time BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 测试数据
INSERT INTO user (username, password, nickname, role) VALUES 
('admin', 'admin123', '管理员', 1),
('girlfriend', 'love123', '小可爱', 0);

INSERT INTO category (name, icon, sort_order) VALUES 
('早餐', 'sunrise', 1),
('午餐', 'sun', 2),
('晚餐', 'moon', 3);

INSERT INTO dish (category_id, name, description, difficulty, cook_time) VALUES 
(1, '爱心煎蛋', '金黄酥脆的煎蛋', 1, 10),
(2, '番茄炒蛋', '经典家常菜', 2, 20),
(3, '红烧排骨', '软烂入味', 4, 60);
