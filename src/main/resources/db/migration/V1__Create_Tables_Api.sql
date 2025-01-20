CREATE TABLE `category`
(
    `id`   bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `product`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `name`        varchar(255)   DEFAULT NULL,
    `description` varchar(255)   DEFAULT NULL,
    `brand`       varchar(255)   DEFAULT NULL,
    `inventory`   int    NOT NULL,
    `price`       decimal(38, 2) DEFAULT NULL,
    `category_id` bigint         DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `key_product_category_id` (`category_id`),
    CONSTRAINT `fk_product_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
);

CREATE TABLE `image`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `product_id`   bigint       DEFAULT NULL,
    `download_url` varchar(255) DEFAULT NULL,
    `file_name`    varchar(255) DEFAULT NULL,
    `file_type`    varchar(255) DEFAULT NULL,
    `image`        mediumblob,
    PRIMARY KEY (`id`),
    KEY            `key_image_product_id` (`product_id`),
    CONSTRAINT `fk_image_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
);

CREATE TABLE `user`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `email`      varchar(255) DEFAULT NULL,
    `first_name` varchar(255) DEFAULT NULL,
    `last_name`  varchar(255) DEFAULT NULL,
    `password`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_user_email` (`email`)
);

CREATE TABLE `orders`
(
    `order_id`     bigint NOT NULL AUTO_INCREMENT,
    `order_date`   date           DEFAULT NULL,
    `order_status` enum('CANCELLED','DELIVERED','PENDING','PROCESSING','SHIPPED') DEFAULT NULL,
    `total_amount` decimal(38, 2) DEFAULT NULL,
    `user_id`      bigint         DEFAULT NULL,
    PRIMARY KEY (`order_id`),
    KEY            `key_orders_user_id` (`user_id`),
    CONSTRAINT `fk_orders_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `order_item`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `price`      decimal(38, 2) DEFAULT NULL,
    `quantity`   int    NOT NULL,
    `order_id`   bigint         DEFAULT NULL,
    `product_id` bigint         DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY          `key_order_item_product_id` (`product_id`),
    KEY          `key_order_item_order_id` (`order_id`),
    CONSTRAINT `fk_order_item_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
    CONSTRAINT `fk_order_item_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
);

CREATE TABLE `cart`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `total_amount` decimal(38, 2) DEFAULT NULL,
    `user_id`      bigint         DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_user_id` (`user_id`),
    CONSTRAINT `fk_cart_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `cart_item`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `quantity`    int    NOT NULL,
    `total_price` decimal(38, 2) DEFAULT NULL,
    `unit_price`  decimal(38, 2) DEFAULT NULL,
    `cart_id`     bigint         DEFAULT NULL,
    `product_id`  bigint         DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `key_cart_item_cart_id` (`cart_id`),
    KEY           `key_cart_item_product_id` (`product_id`),
    CONSTRAINT `fk_cart_item_cart_id` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
    CONSTRAINT `fk_cart_item_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
);

CREATE TABLE `role`
(
    `id`   bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `user_roles`
(
    `user_id` bigint NOT NULL,
    `role_id` bigint NOT NULL,
    KEY       `key_user_roles_role_id` (`role_id`),
    KEY       `key_user_roles_user_id` (`user_id`),
    CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
);
