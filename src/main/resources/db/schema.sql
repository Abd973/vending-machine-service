# drop Table if exists products;
# drop Table if exists jwt_tokens;
# drop Table if exists users;

CREATE TABLE IF NOT EXISTS `users`
(
    `id`       int                      NOT NULL AUTO_INCREMENT,
    `name`     varchar(20) UNIQUE       NOT NULL,
    `password` varchar(100)             NOT NULL,
    `deposit`  int DEFAULT NULL,
    `role`     ENUM ('BUYER', 'SELLER') NOT NULL,
    PRIMARY KEY (`id`)
) AUTO_INCREMENT = 1;


CREATE TABLE IF NOT EXISTS `products`
(
    `id`        int           NOT NULL AUTO_INCREMENT,
    `name`      varchar(30)   NOT NULL,
    `price`     int DEFAULT 0 NOT NULL,
    `quantity`  int DEFAULT 0 NOT NULL,
    `seller_id` int           NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
)AUTO_INCREMENT = 1;


CREATE TABLE IF NOT EXISTS `jwt_tokens`
(
    `id`        int          NOT NULL AUTO_INCREMENT,
    `token`     varchar(255) NOT NULL,
    `user_id`   int          NOT NULL,
    `is_valid`  boolean      NOT NULL,
    `created_at` DATETIME     NOT NULL,
    `expire_at` DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
)AUTO_INCREMENT = 1;
