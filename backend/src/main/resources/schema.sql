-- 创建数据库
CREATE DATABASE IF NOT EXISTS cinema_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE cinema_system;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(500) COMMENT '头像',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN/USER',
    `points` INT DEFAULT 0 COMMENT '积分',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 影片表
DROP TABLE IF EXISTS `movie`;
CREATE TABLE `movie` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL COMMENT '影片名称',
    `poster` VARCHAR(500) COMMENT '海报图片',
    `director` VARCHAR(100) COMMENT '导演',
    `actors` VARCHAR(500) COMMENT '主演',
    `genre` VARCHAR(100) COMMENT '类型',
    `duration` INT COMMENT '时长(分钟)',
    `release_date` DATE COMMENT '上映日期',
    `end_date` DATE COMMENT '下映日期',
    `description` TEXT COMMENT '影片简介',
    `rating` DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
    `country` VARCHAR(50) COMMENT '国家/地区',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0下架 1上映中 2即将上映',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影片表';

-- 放映厅表
DROP TABLE IF EXISTS `hall`;
CREATE TABLE `hall` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '放映厅名称',
    `type` VARCHAR(20) DEFAULT '2D' COMMENT '类型: 2D/3D/IMAX/4DX',
    `seat_rows` INT DEFAULT 8 COMMENT '座位行数',
    `seat_cols` INT DEFAULT 10 COMMENT '座位列数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0停用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='放映厅表';

-- 座位表
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `hall_id` INT NOT NULL COMMENT '放映厅ID',
    `row_num` INT NOT NULL COMMENT '行号',
    `col_num` INT NOT NULL COMMENT '列号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0不可用 1可用',
    UNIQUE KEY `uk_hall_seat` (`hall_id`, `row_num`, `col_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- 场次排片表
DROP TABLE IF EXISTS `screening`;
CREATE TABLE `screening` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `movie_id` INT NOT NULL COMMENT '影片ID',
    `hall_id` INT NOT NULL COMMENT '放映厅ID',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `price` DECIMAL(10,2) NOT NULL COMMENT '票价',
    `discount_price` DECIMAL(10,2) COMMENT '会员折扣价',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0已取消 1售票中 2已结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场次排片表';

-- 订单表
DROP TABLE IF EXISTS `ticket_order`;
CREATE TABLE `ticket_order` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `screening_id` INT NOT NULL COMMENT '场次ID',
    `seats` VARCHAR(500) COMMENT '座位信息JSON',
    `seat_count` INT DEFAULT 1 COMMENT '座位数',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
    `status` VARCHAR(20) DEFAULT 'UNPAID' COMMENT '状态: UNPAID/PAID/CANCELLED/USED/REFUNDED',
    `pay_time` DATETIME COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 收藏表
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL COMMENT '用户ID',
    `movie_id` INT NOT NULL COMMENT '影片ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ========== 测试数据 ==========

-- 管理员和测试用户
INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `points`, `status`) VALUES
('admin', '123456', '系统管理员', 'https://i.pravatar.cc/150?img=11', '13800000000', 'admin@cinema.com', 'ADMIN', 0, 1),
('test', '123456', '测试用户', 'https://i.pravatar.cc/150?img=32', '13800000001', 'test@cinema.com', 'USER', 520, 1),
('zhangsan', '123456', '张三', 'https://i.pravatar.cc/150?img=53', '13800000002', 'zhangsan@cinema.com', 'USER', 300, 1),
('lisi', '123456', '李四', 'https://i.pravatar.cc/150?img=14', '13800000003', 'lisi@cinema.com', 'USER', 150, 1),
('wangwu', '123456', '王五', 'https://i.pravatar.cc/150?img=45', '13800000004', 'wangwu@cinema.com', 'USER', 800, 1);

-- 影片数据（使用TMDb真实电影海报URL）
INSERT INTO `movie` (`name`, `poster`, `director`, `actors`, `genre`, `duration`, `release_date`, `end_date`, `description`, `rating`, `country`, `status`) VALUES
('流浪地球2', 'https://image.tmdb.org/t/p/w500/cAS2e9hUwu6Ydsx7byXj16H00Ai.jpg', '郭帆', '吴京,刘德华,李雪健,沙溢,宁理', '科幻/冒险/灾难', 173, '2025-01-22', '2026-04-22', '太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而，宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战。', 8.3, '中国', 1),
('满江红', 'https://image.tmdb.org/t/p/w500/eqcH2KZZMJUvpYM1SWhch2XpSyp.jpg', '张艺谋', '沈腾,易烊千玺,张译,雷佳音,岳云鹏', '喜剧/悬疑/古装', 159, '2025-01-22', '2026-04-22', '南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈。期间金国使者死在宰相秘书的住所，一个小兵与亲兵营副统领机缘巧合地被裹挟进一场巨大的阴谋之中。', 7.0, '中国', 1),
('阿凡达：水之道', 'https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg', '詹姆斯·卡梅隆', '萨姆·沃辛顿,佐伊·索尔达娜,西格妮·韦弗', '科幻/动作/冒险', 192, '2025-12-16', '2026-05-16', '杰克·萨利和奈提莉组建了家庭，他们的孩子也逐渐成长。然而危险如影随形，萨利一家不得不离开家园，前往潘多拉星球的海洋深处探索。', 8.1, '美国', 1),
('铃芽之旅', 'https://image.tmdb.org/t/p/w500/x8IcS3iaqo1hayup5m9NTFJAqYJ.jpg', '新海诚', '原菜乃华,松村北斗,深津绘里,染谷将太', '动画/奇幻/冒险', 122, '2025-03-24', '2026-06-24', '17岁少女铃芽与一位旅行中的青年相遇，他正在寻找"门"。追随他的脚步，铃芽来到山中废墟发现了一扇古老的门，她仿佛被什么吸引，将手伸向了那扇门……', 7.5, '日本', 1),
('蜘蛛侠：纵横宇宙', 'https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg', '华金·多斯·桑托斯', '沙梅克·摩尔,海莉·斯坦菲尔德,奥斯卡·伊萨克', '动画/动作/冒险', 140, '2025-06-02', '2026-09-02', '迈尔斯·莫拉莱斯在多元宇宙中展开新冒险，面对多元宇宙中的全新威胁，他必须和其他蜘蛛侠联手，共同对抗这一全新危机。', 8.6, '美国', 1),
('封神第一部', 'https://image.tmdb.org/t/p/w500/n3Vy8oJEGqvaFurE7e7QJbF4mGs.jpg', '乌尔善', '费翔,李雪健,黄渤,于适,陈牧驰', '奇幻/冒险/古装', 148, '2025-07-20', '2026-10-20', '殷商末年，纣王帝辛暴虐无道，以人祭天。西伯侯之子姬发觉醒天命，踏上了封神之路，一场波澜壮阔的神话战争就此展开。', 7.8, '中国', 1),
('奥本海默', 'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg', '克里斯托弗·诺兰', '基里安·墨菲,小罗伯特·唐尼,艾米莉·布朗特,马特·达蒙', '剧情/传记/历史', 180, '2025-08-30', '2026-11-30', '影片改编自凯·伯德和马丁·J·舍温所著的《奥本海默传》，讲述了美国理论物理学家奥本海默主导制造出原子弹的故事。', 8.9, '美国', 1),
('名侦探柯南：黑铁的鱼影', 'https://image.tmdb.org/t/p/w500/h2ZJJ0pSiPOvyP5Zoo52JREWCeP.jpg', '立川让', '高山南,山崎和佳奈,林原惠美', '动画/悬疑', 109, '2025-04-14', '2026-07-14', '柯南和灰原哀等人来到八丈岛附近建造的太平洋浮标上，在这里发生了工作人员被杀害的案件，而黑衣组织的身影也悄然出现。', 7.2, '日本', 2),
('哥斯拉大战金刚2：帝国崛起', 'https://image.tmdb.org/t/p/w500/z1p34vh7dEOnLDmyCrlUVLuoDzd.jpg', '亚当·温加德', '丽贝卡·豪尔,布莱恩·泰里·亨利,丹·史蒂文斯', '科幻/动作/冒险', 115, '2026-04-12', '2026-07-12', '两大巨兽再次联手对抗一个隐藏在我们世界中的巨大威胁。这部新续作将深入探索这些巨兽的历史以及它们在空心地球的起源。', 7.0, '美国', 2),
('功夫熊猫4', 'https://image.tmdb.org/t/p/w500/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg', '迈克·米切尔', '杰克·布莱克,奥卡菲娜,维奥拉·戴维斯', '喜剧/动画/冒险', 94, '2026-04-01', '2026-07-01', '阿宝被选为和平谷的精神领袖，他必须找到并训练一位新的神龙大侠，同时面对一位名叫变色龙的强大反派的威胁。', 7.3, '美国', 2);

-- 放映厅数据
INSERT INTO `hall` (`name`, `type`, `seat_rows`, `seat_cols`, `status`) VALUES
('1号激光厅', 'IMAX', 10, 14, 1),
('2号杜比厅', '3D', 8, 12, 1),
('3号标准厅', '2D', 8, 10, 1),
('4号标准厅', '2D', 8, 10, 1),
('5号VIP厅', '3D', 6, 8, 1),
('6号4DX动感厅', '4DX', 6, 10, 1);

-- 为每个放映厅创建座位（使用存储过程简化）
-- 1号激光厅 10x14
INSERT INTO `seat` (`hall_id`, `row_num`, `col_num`, `status`)
SELECT 1, r.num, c.num, 1
FROM (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) r
CROSS JOIN (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14) c;

-- 2号杜比厅 8x12
INSERT INTO `seat` (`hall_id`, `row_num`, `col_num`, `status`)
SELECT 2, r.num, c.num, 1
FROM (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) r
CROSS JOIN (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) c;

-- 3号标准厅 8x10
INSERT INTO `seat` (`hall_id`, `row_num`, `col_num`, `status`)
SELECT 3, r.num, c.num, 1
FROM (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) r
CROSS JOIN (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) c;

-- 4号标准厅 8x10
INSERT INTO `seat` (`hall_id`, `row_num`, `col_num`, `status`)
SELECT 4, r.num, c.num, 1
FROM (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) r
CROSS JOIN (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) c;

-- 5号VIP厅 6x8
INSERT INTO `seat` (`hall_id`, `row_num`, `col_num`, `status`)
SELECT 5, r.num, c.num, 1
FROM (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) r
CROSS JOIN (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) c;

-- 6号4DX动感厅 6x10
INSERT INTO `seat` (`hall_id`, `row_num`, `col_num`, `status`)
SELECT 6, r.num, c.num, 1
FROM (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) r
CROSS JOIN (SELECT 1 num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) c;

-- 禁用部分座位（过道/维修座位）
UPDATE `seat` SET `status` = 0 WHERE `hall_id` = 1 AND `row_num` = 1 AND `col_num` IN (1, 14);
UPDATE `seat` SET `status` = 0 WHERE `hall_id` = 2 AND `row_num` = 1 AND `col_num` IN (1, 12);

-- 场次排片数据
INSERT INTO `screening` (`movie_id`, `hall_id`, `start_time`, `end_time`, `price`, `discount_price`, `status`) VALUES
(1, 1, '2026-03-22 09:00:00', '2026-03-22 11:53:00', 68.00, 58.00, 1),
(1, 1, '2026-03-22 13:00:00', '2026-03-22 15:53:00', 78.00, 66.00, 1),
(1, 2, '2026-03-22 19:00:00', '2026-03-22 21:53:00', 88.00, 75.00, 1),
(2, 3, '2026-03-22 10:00:00', '2026-03-22 12:39:00', 45.00, 38.00, 1),
(2, 3, '2026-03-22 14:00:00', '2026-03-22 16:39:00', 50.00, 42.00, 1),
(3, 1, '2026-03-22 16:00:00', '2026-03-22 19:12:00', 98.00, 83.00, 1),
(3, 6, '2026-03-22 20:00:00', '2026-03-22 23:12:00', 128.00, 108.00, 1),
(4, 4, '2026-03-22 10:30:00', '2026-03-22 12:32:00', 42.00, 35.00, 1),
(4, 4, '2026-03-22 14:30:00', '2026-03-22 16:32:00', 48.00, 40.00, 1),
(5, 2, '2026-03-22 10:00:00', '2026-03-22 12:20:00', 55.00, 46.00, 1),
(5, 2, '2026-03-22 15:00:00', '2026-03-22 17:20:00', 60.00, 50.00, 1),
(6, 1, '2026-03-23 09:30:00', '2026-03-23 12:00:00', 72.00, 61.00, 1),
(6, 3, '2026-03-23 14:00:00', '2026-03-23 16:28:00', 52.00, 44.00, 1),
(7, 5, '2026-03-23 18:00:00', '2026-03-23 21:00:00', 88.00, 75.00, 1),
(7, 1, '2026-03-23 21:00:00', '2026-03-24 00:00:00', 98.00, 83.00, 1),
(1, 2, '2026-03-23 09:00:00', '2026-03-23 11:53:00', 65.00, 55.00, 1),
(3, 6, '2026-03-23 13:00:00', '2026-03-23 16:12:00', 118.00, 100.00, 1),
(5, 4, '2026-03-23 10:00:00', '2026-03-23 12:20:00', 45.00, 38.00, 1),
(4, 3, '2026-03-24 09:30:00', '2026-03-24 11:32:00', 39.00, 33.00, 1),
(2, 4, '2026-03-24 13:00:00', '2026-03-24 15:39:00', 45.00, 38.00, 1);

-- 订单数据
INSERT INTO `ticket_order` (`order_no`, `user_id`, `screening_id`, `seats`, `seat_count`, `total_price`, `status`, `pay_time`, `create_time`) VALUES
('ORD20260320001', 2, 1, '[{"row":3,"col":5},{"row":3,"col":6}]', 2, 136.00, 'PAID', '2026-03-20 08:30:00', '2026-03-20 08:25:00'),
('ORD20260320002', 3, 4, '[{"row":5,"col":4}]', 1, 45.00, 'PAID', '2026-03-20 09:15:00', '2026-03-20 09:10:00'),
('ORD20260320003', 4, 6, '[{"row":4,"col":7},{"row":4,"col":8}]', 2, 196.00, 'PAID', '2026-03-20 10:00:00', '2026-03-20 09:55:00'),
('ORD20260320004', 2, 10, '[{"row":6,"col":3},{"row":6,"col":4},{"row":6,"col":5}]', 3, 165.00, 'PAID', '2026-03-20 11:30:00', '2026-03-20 11:25:00'),
('ORD20260320005', 5, 3, '[{"row":7,"col":6},{"row":7,"col":7}]', 2, 176.00, 'PAID', '2026-03-20 12:00:00', '2026-03-20 11:55:00'),
('ORD20260320006', 3, 8, '[{"row":2,"col":5}]', 1, 42.00, 'USED', '2026-03-19 10:00:00', '2026-03-19 09:55:00'),
('ORD20260320007', 2, 5, '[{"row":4,"col":3},{"row":4,"col":4}]', 2, 100.00, 'CANCELLED', NULL, '2026-03-20 14:00:00'),
('ORD20260320008', 4, 12, '[{"row":5,"col":5},{"row":5,"col":6}]', 2, 144.00, 'UNPAID', NULL, '2026-03-21 20:00:00'),
('ORD20260321001', 2, 14, '[{"row":3,"col":3},{"row":3,"col":4}]', 2, 176.00, 'PAID', '2026-03-21 09:00:00', '2026-03-21 08:55:00'),
('ORD20260321002', 5, 2, '[{"row":5,"col":7}]', 1, 78.00, 'PAID', '2026-03-21 10:30:00', '2026-03-21 10:25:00');

-- 收藏数据
INSERT INTO `collection` (`user_id`, `movie_id`) VALUES
(2, 1), (2, 3), (2, 5),
(3, 1), (3, 2), (3, 7),
(4, 3), (4, 4), (4, 6),
(5, 1), (5, 5), (5, 7);
