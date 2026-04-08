-- 女友点餐系统数据库初始化脚本
-- 设置字符集确保中文正常显示
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS girlfriend_menu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE girlfriend_menu;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0-女友 1-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 菜品表
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `difficulty` TINYINT NOT NULL DEFAULT 3 COMMENT '难度：1-5星',
    `cook_time` INT NOT NULL DEFAULT 30 COMMENT '烹饪时间(分钟)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `order_count` INT NOT NULL DEFAULT 0 COMMENT '被点次数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品表';

-- 点餐记录表
DROP TABLE IF EXISTS `order_record`;
CREATE TABLE `order_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `meal_type` VARCHAR(20) NOT NULL COMMENT '餐次：breakfast/lunch/dinner/snack',
    `order_date` DATE NOT NULL COMMENT '点餐日期',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待做 1-已做 2-取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_dish_id` (`dish_id`),
    KEY `idx_order_date` (`order_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点餐记录表';

-- 心愿单表
DROP TABLE IF EXISTS `wishlist`;
CREATE TABLE `wishlist` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '心愿ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `dish_name` VARCHAR(100) NOT NULL COMMENT '想吃的菜名',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待添加 1-已添加',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心愿单表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `operation` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `cost_time` BIGINT DEFAULT NULL COMMENT '耗时(ms)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 初始化数据 - 真实中文数据
-- ============================================

-- 用户账号 (密码使用 BCrypt 加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `role`) VALUES
('admin', '$2b$10$lOpskV0Rimr.bZZKn4xnPudEZ.7acvgC4D02.pCp35KCDOXAT9uoS', '男朋友', NULL, 1),
('girlfriend', '$2b$10$hndBe506mV/m5n4ImuX4jeewEnAs2e.oG22ErTi6Fcar7gV9s1imC', '小可爱', NULL, 0);

-- 分类数据
INSERT INTO `category` (`name`, `icon`, `sort_order`) VALUES
('早餐', 'sunrise', 1),
('午餐', 'sun', 2),
('晚餐', 'moon', 3),
('夜宵', 'star', 4),
('甜点', 'cake', 5),
('饮品', 'coffee', 6);

-- 早餐菜品 (category_id = 1)
INSERT INTO `dish` (`category_id`, `name`, `description`, `difficulty`, `cook_time`, `order_count`) VALUES
(1, '爱心煎蛋', '金黄酥脆的太阳蛋，撒上少许黑胡椒和盐，简单却充满爱意', 1, 10, 15),
(1, '牛奶燕麦粥', '进口燕麦搭配纯牛奶，加入蜂蜜和坚果，营养又美味', 1, 15, 12),
(1, '火腿芝士三明治', '吐司夹着火腿片和芝士，煎至金黄酥脆，早餐必备', 2, 15, 18),
(1, '皮蛋瘦肉粥', '软糯的白粥配上皮蛋和瘦肉丝，撒上葱花，暖胃又暖心', 2, 40, 8),
(1, '葱油拌面', '细面条拌上自制葱油，简单快手的早餐选择', 1, 15, 6),
(1, '鸡蛋灌饼', '外酥里嫩的灌饼，配上生菜和甜面酱，好吃到停不下来', 3, 25, 10);

-- 午餐菜品 (category_id = 2)
INSERT INTO `dish` (`category_id`, `name`, `description`, `difficulty`, `cook_time`, `order_count`) VALUES
(2, '番茄炒蛋', '经典家常菜，酸甜的番茄配上嫩滑的鸡蛋，米饭杀手', 2, 15, 25),
(2, '红烧排骨', '精选肋排慢炖入味，肉质软烂，酱香浓郁，超级下饭', 4, 60, 20),
(2, '可乐鸡翅', '鸡翅用可乐焖煮，甜甜的味道，女生最爱的一道菜', 3, 35, 28),
(2, '青椒肉丝', '嫩滑的肉丝配上清脆的青椒，简单又好吃', 2, 20, 15),
(2, '鱼香肉丝', '酸甜微辣的经典川菜，虽然没有鱼但超级香', 3, 25, 18),
(2, '蒜蓉西兰花', '翠绿的西兰花配上蒜蓉，清淡健康又美味', 1, 15, 12),
(2, '土豆炖牛肉', '软糯的土豆和入味的牛肉，冬天的温暖选择', 4, 90, 16),
(2, '酸辣土豆丝', '脆爽的土豆丝，酸辣开胃，下饭神器', 2, 20, 22);

-- 晚餐菜品 (category_id = 3)
INSERT INTO `dish` (`category_id`, `name`, `description`, `difficulty`, `cook_time`, `order_count`) VALUES
(3, '清蒸鲈鱼', '新鲜鲈鱼清蒸，保留原汁原味，鲜嫩可口', 3, 25, 14),
(3, '宫保鸡丁', '微辣开胃的经典川菜，花生米和鸡丁的完美搭配', 3, 25, 19),
(3, '糖醋里脊', '外酥里嫩的里脊肉，裹上酸甜的糖醋汁，超级好吃', 4, 35, 24),
(3, '麻婆豆腐', '麻辣鲜香的豆腐，配上肉末，米饭的最佳搭档', 3, 20, 17),
(3, '蒜香排骨', '炸至金黄的排骨，撒上蒜蓉，香气扑鼻', 4, 45, 13),
(3, '干煸四季豆', '四季豆煸至表皮微皱，配上肉末，香脆可口', 2, 20, 11),
(3, '红烧茄子', '软糯的茄子吸满酱汁，咸香入味', 2, 25, 15),
(3, '水煮肉片', '麻辣鲜香的水煮肉片，肉嫩汤浓，超级过瘾', 4, 30, 21);

-- 夜宵菜品 (category_id = 4)
INSERT INTO `dish` (`category_id`, `name`, `description`, `difficulty`, `cook_time`, `order_count`) VALUES
(4, '韩式炒年糕', '软糯Q弹的年糕，配上甜辣酱，追剧必备', 2, 20, 16),
(4, '煎饺', '金黄酥脆的煎饺，蘸上醋和辣椒油，完美', 2, 25, 14),
(4, '蛋炒饭', '粒粒分明的蛋炒饭，简单却让人满足', 2, 15, 20),
(4, '炸鸡块', '外酥里嫩的炸鸡块，配上番茄酱，夜宵首选', 3, 30, 18),
(4, '泡面', '深夜的一碗泡面，加个蛋加根肠，幸福感爆棚', 1, 10, 25),
(4, '烤肠', '香喷喷的烤肠，简单快手的夜宵', 1, 15, 12);

-- 甜点 (category_id = 5)
INSERT INTO `dish` (`category_id`, `name`, `description`, `difficulty`, `cook_time`, `order_count`) VALUES
(5, '提拉米苏', '意式经典甜点，咖啡和芝士的完美融合', 4, 60, 10),
(5, '芒果班戟', '清爽的港式甜点，新鲜芒果包裹在薄饼中', 3, 30, 12),
(5, '双皮奶', '顺滑细腻的双皮奶，奶香浓郁', 3, 40, 8),
(5, '红豆沙', '软糯香甜的红豆沙，冬天的暖心甜品', 2, 60, 6),
(5, '蛋挞', '酥脆的外皮配上嫩滑的蛋液，下午茶必备', 3, 45, 14),
(5, '草莓蛋糕', '新鲜草莓搭配奶油蛋糕，甜蜜的味道', 4, 90, 9);

-- 饮品 (category_id = 6)
INSERT INTO `dish` (`category_id`, `name`, `description`, `difficulty`, `cook_time`, `order_count`) VALUES
(6, '珍珠奶茶', '自制珍珠配上香浓奶茶，比外面买的更健康', 2, 30, 22),
(6, '水果茶', '新鲜水果现切现泡，清爽解腻', 1, 15, 18),
(6, '柠檬蜂蜜水', '酸甜的柠檬配上蜂蜜，美容养颜', 1, 5, 15),
(6, '热可可', '浓郁的可可配上棉花糖，冬日暖饮', 1, 10, 10),
(6, '桂花酸梅汤', '酸甜开胃的酸梅汤，夏日解暑神器', 2, 20, 8),
(6, '姜撞奶', '暖胃的姜撞奶，女生生理期的好伙伴', 2, 15, 7);

-- 点餐记录示例数据 (更多数据用于排行榜展示)
INSERT INTO `order_record` (`user_id`, `dish_id`, `meal_type`, `order_date`, `remark`, `status`) VALUES
-- 今天
(2, 3, 'breakfast', CURDATE(), '不要太多酱', 1),
(2, 10, 'lunch', CURDATE(), '多放点辣椒', 1),
-- 昨天
(2, 18, 'dinner', DATE_SUB(CURDATE(), INTERVAL 1 DAY), NULL, 1),
(2, 7, 'lunch', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '少油少盐', 1),
(2, 9, 'lunch', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '多放可乐', 1),
-- 2天前
(2, 25, 'snack', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '加个蛋', 1),
(2, 14, 'dinner', DATE_SUB(CURDATE(), INTERVAL 2 DAY), NULL, 1),
(2, 9, 'dinner', DATE_SUB(CURDATE(), INTERVAL 2 DAY), NULL, 1),
-- 3天前
(2, 31, 'snack', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '少糖', 1),
(2, 9, 'lunch', DATE_SUB(CURDATE(), INTERVAL 3 DAY), NULL, 1),
(2, 7, 'dinner', DATE_SUB(CURDATE(), INTERVAL 3 DAY), NULL, 1),
-- 4天前
(2, 18, 'dinner', DATE_SUB(CURDATE(), INTERVAL 4 DAY), '糖醋汁多一点', 1),
(2, 3, 'breakfast', DATE_SUB(CURDATE(), INTERVAL 4 DAY), NULL, 1),
(2, 10, 'lunch', DATE_SUB(CURDATE(), INTERVAL 4 DAY), NULL, 1),
-- 5天前
(2, 9, 'lunch', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '甜一点', 1),
(2, 18, 'dinner', DATE_SUB(CURDATE(), INTERVAL 5 DAY), NULL, 1),
(2, 25, 'snack', DATE_SUB(CURDATE(), INTERVAL 5 DAY), NULL, 1),
-- 6天前
(2, 7, 'lunch', DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL, 1),
(2, 9, 'dinner', DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL, 1),
(2, 31, 'snack', DATE_SUB(CURDATE(), INTERVAL 6 DAY), '加珍珠', 1),
-- 一周前
(2, 3, 'breakfast', DATE_SUB(CURDATE(), INTERVAL 7 DAY), NULL, 1),
(2, 18, 'dinner', DATE_SUB(CURDATE(), INTERVAL 7 DAY), NULL, 1),
(2, 10, 'lunch', DATE_SUB(CURDATE(), INTERVAL 7 DAY), NULL, 1);

-- 心愿清单示例数据
INSERT INTO `wishlist` (`user_id`, `dish_name`, `description`, `status`) VALUES
(2, '日式咖喱饭', '想吃那种浓郁的日式咖喱，配上炸猪排', 0),
(2, '寿喜烧', '天冷了想吃热乎乎的寿喜烧', 0),
(2, '芝士焗饭', '好久没吃芝士焗饭了，想念那个拉丝的感觉', 0),
(2, '麻辣香锅', '想吃麻辣香锅，多放藕片和土豆', 1),
(2, '烤鱼', '想吃万州烤鱼，要麻辣味的', 0);
