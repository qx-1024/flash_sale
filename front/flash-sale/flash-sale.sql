/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : localhost:3306
 Source Schema         : flash-sale

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 26/03/2025 09:40:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `activityId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '闪购活动id',
  `productId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '闪购商品id',
  `activityName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '闪购活动名称',
  `activityStatus` int NULL DEFAULT 0 COMMENT '闪购活动状态（0：闪购未开始 | 1：闪购已结束 | 2：闪购进行中）',
  `startTime` datetime NOT NULL COMMENT '闪购开始时间',
  `endTime` datetime NOT NULL COMMENT '闪购结束时间',
  `createTime` datetime NOT NULL COMMENT '闪购活动创建时间',
  `updateTime` datetime NULL DEFAULT NULL COMMENT '闪购活动更新时间',
  `isDeleted` int NULL DEFAULT 0 COMMENT '闪购活动是否删除（0：未删除 | 1：已删除）',
  PRIMARY KEY (`activityId`) USING BTREE,
  UNIQUE INDEX `activity_ibfk_1`(`productId` ASC) USING BTREE,
  UNIQUE INDEX `activity_ibfk_2`(`activityId` ASC) USING BTREE,
  CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `product` (`productId`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `orderId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单id',
  `userId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `activityId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '闪购活动id',
  `productId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品id',
  `amount` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '订单备注',
  `orderStatus` int NULL DEFAULT 0 COMMENT '订单状态（0：未完成 | 1：已完成）',
  `payStatus` int NULL DEFAULT 0 COMMENT '支付状态（0：未支付 | 1：已支付）',
  `shippingStatus` int NULL DEFAULT 0 COMMENT '运输状态（0：未发货 | 1：已发货 | 2：已收货）',
  `createTime` datetime NOT NULL COMMENT '订单创建时间',
  `updateTime` datetime NULL DEFAULT NULL COMMENT '订单修改时间',
  `isDeleted` int NULL DEFAULT 0 COMMENT '订单是否已删除（0：未删除 | 1：已删除）',
  PRIMARY KEY (`orderId`) USING BTREE,
  UNIQUE INDEX `orderId`(`orderId` ASC) USING BTREE,
  INDEX `productId`(`productId` ASC) USING BTREE,
  INDEX `userId`(`userId` ASC) USING BTREE,
  INDEX `activityId`(`activityId` ASC) USING BTREE,
  INDEX `orderStatus`(`orderStatus` ASC) USING BTREE,
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`activityId`) REFERENCES `activity` (`activityId`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `order_ibfk_3` FOREIGN KEY (`productId`) REFERENCES `product` (`productId`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `productId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品id',
  `productName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `isFlashSale` int NULL DEFAULT 0 COMMENT '是否参与闪购（0：不参与 | 1：参与）',
  `price` decimal(10, 2) NOT NULL COMMENT '闪购价格（单价：元/件）',
  `stock` int NOT NULL COMMENT '商品库存',
  `sales` int NULL DEFAULT 0 COMMENT '商品销量',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品图片',
  `createTime` datetime NOT NULL COMMENT '商品创建时间',
  `updateTime` datetime NULL DEFAULT NULL COMMENT '商品更新时间',
  `isDeleted` int NULL DEFAULT 0 COMMENT '商品是否删除（0：未删除 | 1：已删除）',
  PRIMARY KEY (`productId`) USING BTREE,
  UNIQUE INDEX `product_ibfk_1`(`productId` ASC) USING BTREE,
  INDEX `product_ibfk_2`(`isFlashSale` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation`  (
  `reservationId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约活动id',
  `activityId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约的闪购活动 id',
  `reservationName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约活动名称',
  `reservationStatus` int NULL DEFAULT 0 COMMENT '预约状态（0：预约未开始 | 1：预约已结束 | 2：预约进行中）',
  `startTime` datetime NOT NULL COMMENT '预约活动开始时间',
  `endTime` datetime NOT NULL COMMENT '预约活动结束时间',
  `createTime` datetime NOT NULL COMMENT '预约活动创建时间',
  `updateTime` datetime NULL DEFAULT NULL COMMENT '预约活动更新时间',
  `isDeleted` int NULL DEFAULT 0 COMMENT '预约活动是否删除（0：未删除 | 1：已删除）',
  PRIMARY KEY (`reservationId`) USING BTREE,
  UNIQUE INDEX `reservation_ibfk_1`(`activityId` ASC) USING BTREE,
  UNIQUE INDEX `reservation_ibfk_3`(`reservationId` ASC) USING BTREE,
  INDEX `reservation_ibfk_2`(`reservationStatus` ASC) USING BTREE,
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`activityId`) REFERENCES `activity` (`activityId`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for reservation_user
-- ----------------------------
DROP TABLE IF EXISTS `reservation_user`;
CREATE TABLE `reservation_user`  (
  `id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约信息id',
  `reservationId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约活动id',
  `userId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `isDeleted` int NULL DEFAULT 0 COMMENT '预约是否删除（0：未删除 | 1：已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `reservation_user__ibfk_1`(`reservationId` ASC) USING BTREE,
  INDEX `reservation_user__ibfk_2`(`userId` ASC) USING BTREE,
  INDEX `reservation_user__ibfk_3`(`createTime` ASC) USING BTREE,
  INDEX `reservation_user__ibfk_4`(`reservationId` ASC, `userId` ASC) USING BTREE,
  CONSTRAINT `reservation_user__ibfk_1` FOREIGN KEY (`reservationId`) REFERENCES `reservation` (`reservationId`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `reservation_user__ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `userId` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户 id',
  `realName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户真实姓名',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `phoneNumber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户手机号',
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户地址',
  `userType` int NULL DEFAULT 0 COMMENT '用户类型（0：普通用户 | 1：VIP 用户）',
  `gender` int NULL DEFAULT 2 COMMENT '用户性别（0：男 | 1：女 | 2：未知）',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户账号创建时间',
  `updateTime` datetime NULL DEFAULT NULL COMMENT '用户账号更新时间',
  `isDeleted` int NULL DEFAULT 0 COMMENT '用户是否删除（0：未删除 | 1：已删除）',
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE INDEX `userid`(`userId` ASC) USING BTREE,
  UNIQUE INDEX `account`(`account` ASC) USING BTREE,
  INDEX `gender`(`gender` ASC) USING BTREE,
  INDEX `usertype`(`userType` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
